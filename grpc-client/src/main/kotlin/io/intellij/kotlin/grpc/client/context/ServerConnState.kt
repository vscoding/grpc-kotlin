package io.intellij.kotlin.grpc.client.context

import io.intellij.kotlin.grpc.context.NetworkAddr

/**
 * ServerConn
 *
 * @author dev@intellij.io
 */
data class ServerConnState(
  val connected: Boolean = false,
  val remote: NetworkAddr,
  val local: NetworkAddr,
) {
  companion object {
    val DEFAULT: ServerConnState = ServerConnState(false, NetworkAddr.UNKNOWN_REMOTE, NetworkAddr.UNKNOWN_LOCAL)

    fun create(remote: NetworkAddr, local: NetworkAddr): ServerConnState {
      return ServerConnState(true, remote, local)
    }
  }

}
