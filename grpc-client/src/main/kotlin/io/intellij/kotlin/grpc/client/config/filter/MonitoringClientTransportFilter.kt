package io.intellij.kotlin.grpc.client.config.filter

import io.grpc.Attributes
import io.grpc.ClientTransportFilter
import io.grpc.Grpc
import io.intellij.kotlin.grpc.client.config.getLogger
import io.intellij.kotlin.grpc.client.context.RegistryOperator
import io.intellij.kotlin.grpc.context.Address

/**
 * MonitoringClientTransportFilter
 *
 * @author tech@intellij.io
 */
class MonitoringClientTransportFilter(
    private val registryOperator: RegistryOperator
) : ClientTransportFilter() {
    private val log = getLogger(MonitoringClientTransportFilter::class.java)

    override fun transportReady(transportAttrs: Attributes): Attributes? {
        log.debug("transport ready; {}", transportAttrs)
        val remote: Address = Address.from(transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)!!, false)
        val local: Address = Address.from(transportAttrs.get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR)!!, true)
        registryOperator.connect(remote.host, remote.port, local.port)
        return super.transportReady(transportAttrs)
    }

    override fun transportTerminated(transportAttrs: Attributes?) {
        log.debug("transport terminated; {}", transportAttrs)
        registryOperator.disconnect()
    }

}
