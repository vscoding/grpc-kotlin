package io.intellij.kotlin.grpc.server.context

import io.intellij.kotlin.grpc.context.NetworkAddr
import java.util.Objects

/**
 * ClientConnState
 *
 * @author dev@intellij.io
 */
class ClientConnState(val connected: Boolean, val remote: NetworkAddr) {

  override fun hashCode(): Int {
    return Objects.hash(connected, remote)
  }

  override fun equals(other: Any?): Boolean {
    if (Objects.isNull(other)) {
      return false
    }
    if (other is ClientConnState) {
      return this.connected == other.connected && this.remote.equals(other.remote)
    }
    return false
  }

  override fun toString(): String {
    return "ClientConnState(connected=$connected, remote=$remote)"
  }

  companion object {
    fun up(networkAddr: NetworkAddr): ClientConnState {
      return ClientConnState(true, networkAddr)
    }

    fun down(networkAddr: NetworkAddr): ClientConnState {
      return ClientConnState(false, networkAddr)
    }
  }

}