package io.intellij.kotlin.grpc.server.context

import io.intellij.kotlin.grpc.context.Address
import java.util.Objects

/**
 * ClientConn
 *
 * @author tech@intellij.io
 */
class ClientConn(val connected: Boolean, val remote: Address) {

    override fun hashCode(): Int {
        return Objects.hash(connected, remote)
    }

    override fun equals(other: Any?): Boolean {
        if (Objects.isNull(other)) {
            return false
        }
        if (other is ClientConn) {
            return this.connected == other.connected && this.remote.equals(other.remote)
        }
        return false
    }

    override fun toString(): String {
        return "ClientConn(connected=$connected, remote=$remote)"
    }

    companion object {
        fun up(address: Address): ClientConn {
            return ClientConn(true, address)
        }

        fun down(address: Address): ClientConn {
            return ClientConn(false, address)
        }
    }

}