package io.intellij.kotlin.grpc.server.filter

import io.grpc.Attributes
import io.grpc.Grpc
import io.grpc.ServerTransportFilter
import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.context.Address
import io.intellij.kotlin.grpc.server.context.RegistryService
import java.net.InetSocketAddress

/**
 * MonitoringServerTransportFilter
 *
 * @author tech@intellij.io
 */
class MonitoringServerTransportFilter(
  val registryService: RegistryService,
) : ServerTransportFilter() {

  companion object {
    private val log = getLogger(MonitoringServerTransportFilter::class.java)
  }

  override fun transportReady(transportAttrs: Attributes): Attributes? {
    log.info("transport ready: {}", transportAttrs)
    val remoteSocketAddress = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)
    if (remoteSocketAddress == null) {
      log.warn("transport ready without remote address: {}", transportAttrs)
      return super.transportReady(transportAttrs)
    }
    registryService.markUp(
      Address.from(remoteSocketAddress, false),
    )
    return super.transportReady(transportAttrs)
  }

  override fun transportTerminated(transportAttrs: Attributes) {
    val remoteSocketAddress = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)
    if (remoteSocketAddress == null) {
      log.warn("transport terminated without remote address: {}", transportAttrs)
      super.transportTerminated(transportAttrs)
      return
    }
    registryService.markDown(
      Address.from(remoteSocketAddress, false),
    )
    if (remoteSocketAddress is InetSocketAddress) {
      log.info("transport terminated: ip = {} ;port = {}", remoteSocketAddress.hostString, remoteSocketAddress.port)
    } else {
      log.info("transport terminated: {}", transportAttrs)
    }
    super.transportTerminated(transportAttrs)
  }
}
