package io.intellij.kotlin.grpc.server.context

import io.intellij.kotlin.grpc.context.Address
import io.intellij.kotlin.grpc.server.config.getLogger
import org.springframework.stereotype.Service
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * ClientConnOperator
 *
 * @author tech@intellij.io
 */
interface RegistryOperator {
    /**
     * Moves the given address up.
     *
     * @param address the address to be moved up
     */
    fun up(address: Address)

    /**
     * Moves the given address down.
     *
     * @param address the address to be moved down
     */
    fun down(address: Address)

    /**
     * Retrieves the list of connected clients.
     *
     * @return A List of ClientConn objects representing the connected clients.
     */
    fun getLiveClients(): List<ClientConn>

    /**
     * Retrieves the list of ClientConn objects representing the historical connected clients.
     *
     * @return A List of ClientConn objects representing the historical connected clients.
     */
    fun getHistoryClients(): List<ClientConn>

    /**
     * Clears the history of clients.
     */
    fun clearHistoryClients()

    @Service
    class DefaultSharedOperator(
        val clientConnRegistry: ClientConnRegistry
    ) : RegistryOperator {
        private val log = getLogger(DefaultSharedOperator::class.java)

        private val lock: Lock = ReentrantLock()

        override fun up(address: Address) {
            lock.lock()
            try {
                clientConnRegistry.live[address] = ClientConn.up(address)
            } finally {
                lock.unlock()
            }
        }

        override fun down(address: Address) {
            lock.lock()
            try {
                clientConnRegistry.live.remove(address)
                log.info("add history")
                clientConnRegistry.history.add(ClientConn.down(address))
            } finally {
                lock.unlock()
            }
        }

        override fun getLiveClients(): List<ClientConn> = clientConnRegistry.live.values.toList()

        override fun getHistoryClients(): List<ClientConn> = clientConnRegistry.live.values.toList()

        override fun clearHistoryClients() {
            lock.lock()
            try {
                clientConnRegistry.clearHistoryClients()
            } finally {
                lock.unlock()
            }
        }
    }
}
