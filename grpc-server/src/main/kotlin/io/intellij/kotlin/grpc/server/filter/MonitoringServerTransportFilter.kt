package io.intellij.kotlin.grpc.server.filter

import io.grpc.Attributes
import io.grpc.Grpc
import io.grpc.ServerTransportFilter
import io.intellij.kotlin.grpc.context.Address
import io.intellij.kotlin.grpc.server.config.getLogger
import io.intellij.kotlin.grpc.server.context.RegistryOperator
import java.net.InetSocketAddress

/**
 * MonitoringServerTransportFilter
 *
 * @author tech@intellij.io
 */
class MonitoringServerTransportFilter(val registryOperator: RegistryOperator) : ServerTransportFilter() {
    private val log = getLogger(MonitoringServerTransportFilter::class.java)

    override fun transportReady(transportAttrs: Attributes): Attributes? {
        log.info("Transport is ready: {}", transportAttrs)
        registryOperator.up(
            Address.from(transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)!!, false)
        )
        return super.transportReady(transportAttrs)
    }

    override fun transportTerminated(transportAttrs: Attributes) {
        registryOperator.down(
            Address.from(transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)!!, false)
        )
        val remote = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)
        if (remote is InetSocketAddress) {
            log.error("Transport terminated: ip = {} ;port = {}", remote.hostString, remote.port)
        } else {
            log.error("Transport is terminated: {}", transportAttrs)
        }
        super.transportTerminated(transportAttrs)
    }
}
