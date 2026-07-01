package io.intellij.kotlin.grpc.server.context

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
   * Marks the specified client address as active or connected.
   *
   * @param client the address of the client to be marked up
   */
  fun markUp(client: Address)

  /**
   * Marks the specified client address as inactive or disconnected.
   *
   * @param client the address of the client to be marked down
   */
  fun markDown(client: Address)

  /**
   * Retrieves the list of currently connected clients.
   *
   * @return A List of ClientConn objects representing the currently live-connected clients.
   */
  fun getLiveClients(): List<ClientConn>

  /**
   * Retrieves the list of historical client connections.
   *
   * @return A list of ClientConn objects representing the clients that have connected in the past.
   */
  fun getHistoryClients(): List<ClientConn>

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
  val clientConnRuntime: ClientConnRuntime,
) : RegistryService {
  companion object {
    private val log = getLogger(DefaultRegistryService::class.java)
    private const val MAX_HISTORY_CLIENTS = 1024
  }

  private val lock: Lock = ReentrantLock()

  override fun markUp(client: Address) {
    lock.lock()
    try {
      clientConnRuntime.live[client] = ClientConn.up(client)
    } finally {
      lock.unlock()
    }
  }

  override fun markDown(client: Address) {
    lock.lock()
    try {
      clientConnRuntime.live.remove(client)
      log.info("add history")
      clientConnRuntime.history.addLast(ClientConn.down(client))
      while (clientConnRuntime.history.size > MAX_HISTORY_CLIENTS) {
        clientConnRuntime.history.removeFirst()
      }
    } finally {
      lock.unlock()
    }
  }

  override fun getLiveClients(): List<ClientConn> {
    lock.lock()
    try {
      return clientConnRuntime.live.values.toList()
    } finally {
      lock.unlock()
    }
  }

  override fun getHistoryClients(): List<ClientConn> {
    lock.lock()
    try {
      return clientConnRuntime.history.toList()
    } finally {
      lock.unlock()
    }
  }

  override fun clearHistoryClients() {
    lock.lock()
    try {
      clientConnRuntime.clearHistoryClients()
    } finally {
      lock.unlock()
    }
  }
}
