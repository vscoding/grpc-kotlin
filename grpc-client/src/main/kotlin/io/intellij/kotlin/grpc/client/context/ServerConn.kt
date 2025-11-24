package io.intellij.kotlin.grpc.client.context

import io.intellij.kotlin.grpc.context.Address

/**
 * ServerConn
 *
 * @author tech@intellij.io
 */
data class ServerConn(
    val connected: Boolean = false,
    val remote: Address,
    val local: Address
) {
    companion object {
        val DEFAULT: ServerConn = ServerConn(false, Address.UNKNOWN_REMOTE, Address.UNKNOWN_LOCAL)

        fun create(remoteHost: String, remotePort: Int, localPort: Int): ServerConn {
            return ServerConn(true, Address(remoteHost, remotePort), Address(Address.LOCAL_HOST, localPort))
        }
    }
}
