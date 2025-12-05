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
    val registryService: RegistryService
) : ServerTransportFilter() {

    companion object {
        private val log = getLogger(MonitoringServerTransportFilter::class.java)
    }

    override fun transportReady(transportAttrs: Attributes): Attributes? {
        log.info("transport ready: {}", transportAttrs)
        registryService.markUp(
            Address.from(transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)!!, false)
        )
        return super.transportReady(transportAttrs)
    }

    override fun transportTerminated(transportAttrs: Attributes) {
        registryService.markDown(
            Address.from(transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)!!, false)
        )
        val client = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)
        if (client is InetSocketAddress) {
            log.error("transport terminated: ip = {} ;port = {}", client.hostString, client.port)
        } else {
            log.error("transport terminated: {}", transportAttrs)
        }
        super.transportTerminated(transportAttrs)
    }
}
