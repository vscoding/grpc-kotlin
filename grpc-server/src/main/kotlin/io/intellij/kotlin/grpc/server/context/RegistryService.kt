package io.intellij.kotlin.grpc.server.context

import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.context.NetworkAddr
import org.springframework.stereotype.Service
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * RegistryService
 *
 * @author dev@intellij.io
 */
interface RegistryService {

  /**
   * Marks the specified client address as active or connected.
   *
   * @param client the address of the client to be marked up
   */
  fun markUp(client: NetworkAddr)

  /**
   * Marks the specified client address as inactive or disconnected.
   *
   * @param client the address of the client to be marked down
   */
  fun markDown(client: NetworkAddr)

  /**
   * Retrieves the list of currently connected clients.
   *
   * @return A List of ClientConn objects representing the currently live-connected clients.
   */
  fun getLiveClients(): List<ClientConnState>

  /**
   * Retrieves the list of historical client connections.
   *
   * @return A list of ClientConn objects representing the clients that have connected in the past.
   */
  fun getHistoryClients(): List<ClientConnState>

  /**
   * Clears the history of previously connected clients.
   *
   * This function removes all entries from the historical client connections list,
   * ensuring that past connection data is erased. Useful for maintaining a controlled
   * or limited history of client connections.
   */
  fun clearHistoryClients()

}


@Service
class DefaultRegistryService(
  val clientConnStateRuntime: ClientConnStateRuntime,
) : RegistryService {
  companion object {
    private val log = getLogger(DefaultRegistryService::class.java)
    private const val MAX_HISTORY_CLIENTS = 1024
  }

  private val lock: Lock = ReentrantLock()

  override fun markUp(client: NetworkAddr) {
    lock.lock()
    try {
      clientConnStateRuntime.live[client] = ClientConnState.up(client)
    } finally {
      lock.unlock()
    }
  }

  override fun markDown(client: NetworkAddr) {
    lock.lock()
    try {
      clientConnStateRuntime.live.remove(client)
      log.info("add history")
      clientConnStateRuntime.history.addLast(ClientConnState.down(client))
      while (clientConnStateRuntime.history.size > MAX_HISTORY_CLIENTS) {
        clientConnStateRuntime.history.removeFirst()
      }
    } finally {
      lock.unlock()
    }
  }

  override fun getLiveClients(): List<ClientConnState> {
    lock.lock()
    try {
      return clientConnStateRuntime.live.values.toList()
    } finally {
      lock.unlock()
    }
  }

  override fun getHistoryClients(): List<ClientConnState> {
    lock.lock()
    try {
      return clientConnStateRuntime.history.toList()
    } finally {
      lock.unlock()
    }
  }

  override fun clearHistoryClients() {
    lock.lock()
    try {
      clientConnStateRuntime.clearHistoryClients()
    } finally {
      lock.unlock()
    }
  }
}
