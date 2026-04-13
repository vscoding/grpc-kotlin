package io.intellij.kotlin.grpc.client.context

import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.context.Address
import org.springframework.stereotype.Service
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * RegistryService
 *
 * @author tech@intellij.io
 */
interface RegistryService {

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
  fun setServerConn(remote: Address, local: Address)

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
  fun getSeverConn(): ServerConn

  /**
   * Establishes a connection between a remote address and a local address.
   * This method updates the server connection details and ensures thread-safe access
   * to prevent concurrent modifications.
   *
   * @param remote The remote address to connect to.
   * @param local The local address initiating the connection.
   */
  fun connect(remote: Address, local: Address)

  /**
   * Disconnects from the currently connected server.
   *
   *
   * This method is used to disconnect from the server that was previously connected using the [.connect] method.
   * After calling this method, the connection to the server will be terminated and the server details will be reset to their default values.
   */
  fun disconnect(remote: Address? = null, local: Address? = null)
}

@Service
class DefaultRegistryService(
  private val severConnRegistry: ServerConnRegistry,
) : RegistryService {

  private val log = getLogger(DefaultRegistryService::class.java)

  private val connLock: Lock = ReentrantLock()

  override fun markServerReady() {
    severConnRegistry.serverReady.set(true)
  }

  override fun markServerNotReady() {
    severConnRegistry.serverReady.set(false)
  }


  override fun isServerReady(): Boolean {
    return severConnRegistry.serverReady.get()
  }

  override fun setServerConn(remote: Address, local: Address) {
    val serverConn: ServerConn = ServerConn.create(remote, local)
    severConnRegistry.serverConn.set(serverConn)
    log.debug("ServerConn {}", serverConn)
  }

  override fun clearServerConn() {
    val oldConn = severConnRegistry.serverConn.getAndSet(ServerConn.DEFAULT)
    log.debug("Clear ServerConn {}", oldConn)
  }

  override fun getSeverConn(): ServerConn {
    return severConnRegistry.serverConn.get()
  }

  override fun connect(remote: Address, local: Address) {
    connLock.lock()
    try {
      this.setServerConn(remote, local)
    } finally {
      connLock.unlock()
    }
  }

  override fun disconnect(remote: Address?, local: Address?) {
    connLock.lock()
    try {
      val expectedConn = if (remote != null && local != null) ServerConn.create(remote, local) else null
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
