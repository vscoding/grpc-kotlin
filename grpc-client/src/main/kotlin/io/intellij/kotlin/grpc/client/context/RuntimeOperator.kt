package io.intellij.kotlin.grpc.client.context

import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.context.NetworkAddr
import org.springframework.stereotype.Service
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * RuntimeOperator
 *
 * @author dev@intellij.io
 */
interface RuntimeOperator {

  /**
   * Marks the server as ready to receive requests.
   *
   * This method updates the server's readiness state, typically changing it
   * to indicate that the server is now prepared to handle incoming operations or connections.
   */
  fun markServerReady()


  /**
   * Marks the server as not ready to receive requests.
   *
   * This method updates the server's readiness state, typically changing it to indicate
   * that the server is no longer prepared to handle incoming operations or connections.
   */
  fun markServerNotReady()

  /**
   * Returns the status of the server readiness.
   *
   * @return `true` if the server is ready, `false` otherwise.
   */
  fun isServerReady(): Boolean

  /**
   * Updates the server connection details.
   *
   * This method sets the remote and local addresses for the server connection, updating the current state
   * of the server connection data structure.
   *
   * @param remote The remote address to associate with the server connection.
   * @param local The local address to associate with the server connection.
   */
  fun setServerConn(remote: NetworkAddr, local: NetworkAddr)

  /**
   * Clears the server connection details.
   *
   *
   * This method clears the server connection details stored in the SharedOperator interface. After calling this
   * method, the server connection details will be reset to their default values.
   */
  fun clearServerConn()

  /**
   * Retrieves the server connection details.
   *
   * @return The ServerConn object representing the server connection details.
   */
  fun getSeverConn(): ServerConnState

  /**
   * Establishes a connection between a remote address and a local address.
   * This method updates the server connection details and ensures thread-safe access
   * to prevent concurrent modifications.
   *
   * @param remote The remote address to connect to.
   * @param local The local address initiating the connection.
   */
  fun onConnect(remote: NetworkAddr, local: NetworkAddr)

  /**
   * Disconnects from the currently connected server.
   *
   * This method is used to disconnect from the server that was previously connected using the [.connect] method.
   * After calling this method, the connection to the server will be terminated and the server details will be reset to their default values.
   */
  fun onDisconnect(remote: NetworkAddr? = null, local: NetworkAddr? = null)
}

@Service
class DefaultRuntimeOperator(
  private val serverConnStateRuntime: ServerConnStateRuntime,
) : RuntimeOperator {

  private val log = getLogger(DefaultRuntimeOperator::class.java)

  private val connLock: Lock = ReentrantLock()

  override fun markServerReady() {
    serverConnStateRuntime.serverReady.set(true)
  }

  override fun markServerNotReady() {
    serverConnStateRuntime.serverReady.set(false)
  }


  override fun isServerReady(): Boolean {
    return serverConnStateRuntime.serverReady.get()
  }

  override fun setServerConn(remote: NetworkAddr, local: NetworkAddr) {
    val serverConnState: ServerConnState = ServerConnState.create(remote, local)
    serverConnStateRuntime.serverConnState.set(serverConnState)
    log.debug("ServerConn {}", serverConnState)
  }

  override fun clearServerConn() {
    val oldConn = serverConnStateRuntime.serverConnState.getAndSet(ServerConnState.DEFAULT)
    log.debug("Clear ServerConn {}", oldConn)
  }

  override fun getSeverConn(): ServerConnState {
    return serverConnStateRuntime.serverConnState.get()
  }

  override fun onConnect(remote: NetworkAddr, local: NetworkAddr) {
    connLock.lock()
    try {
      this.setServerConn(remote, local)
    } finally {
      connLock.unlock()
    }
  }

  override fun onDisconnect(remote: NetworkAddr?, local: NetworkAddr?) {
    connLock.lock()
    try {
      val expectedConn = if (remote != null && local != null) ServerConnState.create(remote, local) else null
      val currentConn = this.getSeverConn()
      if (expectedConn == null || currentConn == expectedConn) {
        this.clearServerConn()
        this.markServerNotReady()
      } else {
        log.debug("Ignore stale transport termination. current={}, terminated={}", currentConn, expectedConn)
      }
    } finally {
      connLock.unlock()
    }
  }

}
