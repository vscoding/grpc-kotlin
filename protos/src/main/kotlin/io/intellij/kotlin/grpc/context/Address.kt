package io.intellij.kotlin.grpc.context

import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.Objects

/**
 * Address
 *
 * @author tech@intellij.io
 */
class Address(val host: String, val port: Int) {

    override fun hashCode(): Int {
        return Objects.hash(host, port)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Address) {
            this.host == other.host && this.port == other.port
        } else {
            false
        }
    }

    fun copy(): Address {
        return Address(host, port)
    }

    override fun toString(): String {
        return "Address(host='$host', port=$port)"
    }

    companion object {
        private const val UNKNOWN_HOST = "unknown"
        private const val UNKNOWN_PORT = -1
        const val LOCAL_HOST: String = "127.0.0.1"

        val UNKNOWN_REMOTE: Address = Address(UNKNOWN_HOST, UNKNOWN_PORT)
        val UNKNOWN_LOCAL: Address = Address(LOCAL_HOST, UNKNOWN_PORT)

        /**
         * Creates an Address object from a given SocketAddress.
         *
         * @param socketAddress The SocketAddress to convert to an Address.
         * @param local         Whether the SocketAddress is local or not.
         * @return The Address object created from the SocketAddress, or an unknown Address if the SocketAddress is null or not an instance of InetSocketAddress.
         */
        fun from(socketAddress: SocketAddress, local: Boolean): Address {
            return if (socketAddress is InetSocketAddress) {
                Address(socketAddress.hostString, socketAddress.port)
            } else {
                unknown(local)
            }
        }

        private fun unknown(local: Boolean): Address {
            return if (local) UNKNOWN_LOCAL.copy() else UNKNOWN_REMOTE.copy()
        }
    }

}