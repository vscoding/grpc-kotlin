package io.intellij.kotlin.grpc.client.config.filter

import io.grpc.Attributes
import io.grpc.ClientTransportFilter
import io.grpc.Grpc
import io.intellij.kotlin.grpc.client.context.RegistryService
import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.context.Address

/**
 * MonitoringClientTransportFilter
 *
 * @author tech@intellij.io
 */
class MonitoringClientTransportFilter(
  private val registryService: RegistryService,
) : ClientTransportFilter() {
  companion object {
    private val log = getLogger(MonitoringClientTransportFilter::class.java)
  }

  override fun transportReady(transportAttrs: Attributes): Attributes? {
    log.debug("transport ready: {}", transportAttrs)
    val remoteSocketAddress = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)
    val localSocketAddress = transportAttrs.get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR)
    if (remoteSocketAddress == null || localSocketAddress == null) {
      log.warn("transport ready without remote/local address: {}", transportAttrs)
      return super.transportReady(transportAttrs)
    }
    val remote: Address = Address.from(remoteSocketAddress, false)
    val local: Address = Address.from(localSocketAddress, true)
    registryService.connect(remote, local)
    return super.transportReady(transportAttrs)
  }

  override fun transportTerminated(transportAttrs: Attributes?) {
    log.debug("transport terminated: {}", transportAttrs)
    if (transportAttrs == null) {
      registryService.disconnect()
      return
    }
    val remoteSocketAddress = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)
    val localSocketAddress = transportAttrs.get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR)
    if (remoteSocketAddress == null || localSocketAddress == null) {
      registryService.disconnect()
      return
    }
    registryService.disconnect(
      Address.from(remoteSocketAddress, false),
      Address.from(localSocketAddress, true),
    )
  }

}
