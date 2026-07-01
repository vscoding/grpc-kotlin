package io.intellij.kotlin.grpc.client.config.filter

import io.grpc.Attributes
import io.grpc.ClientTransportFilter
import io.grpc.Grpc
import io.intellij.kotlin.grpc.client.context.RuntimeOperator
import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.context.NetworkAddr

/**
 * MonitoringClientTransportFilter
 *
 * @author dev@intellij.io
 */
class MonitoringClientTransportFilter(
  private val runtimeOperator: RuntimeOperator,
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
    val remote: NetworkAddr = NetworkAddr.from(remoteSocketAddress, false)
    val local: NetworkAddr = NetworkAddr.from(localSocketAddress, true)
    runtimeOperator.onConnect(remote, local)
    return super.transportReady(transportAttrs)
  }

  override fun transportTerminated(transportAttrs: Attributes?) {
    log.debug("transport terminated: {}", transportAttrs)
    if (transportAttrs == null) {
      runtimeOperator.onDisconnect()
      return
    }
    val remoteSocketAddress = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)
    val localSocketAddress = transportAttrs.get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR)
    if (remoteSocketAddress == null || localSocketAddress == null) {
      runtimeOperator.onDisconnect()
      return
    }
    runtimeOperator.onDisconnect(
      NetworkAddr.from(remoteSocketAddress, false),
      NetworkAddr.from(localSocketAddress, true),
    )
  }

}
