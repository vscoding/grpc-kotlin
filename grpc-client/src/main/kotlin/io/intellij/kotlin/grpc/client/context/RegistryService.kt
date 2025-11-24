package io.intellij.kotlin.grpc.client.context

import io.intellij.kotlin.grpc.client.config.getLogger
import org.springframework.stereotype.Service
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.Volatile

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
     * Sets the server connection details.
     *
     * @param remoteHost The host address of the remote server.
     * @param remotePort The port number of the remote server.
     * @param localPort  The port number of the local server.
     */
    fun setServerConn(remoteHost: String, remotePort: Int, localPort: Int)

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
     * Connects to a remote host at the specified remote port, using the specified local port.
     *
     * @param remoteHost The host address of the remote server.
     * @param remotePort The port number of the remote server.
     * @param localPort  The port number of the local server.
     */
    fun connect(remoteHost: String, remotePort: Int, localPort: Int)

    /**
     * Disconnects from the currently connected server.
     *
     *
     * This method is used to disconnect from the server that was previously connected using the [.connect] method.
     * After calling this method, the connection to the server will be terminated and the server details will be reset to their default values.
     */
    fun disconnect()


    @Service
    class DefaultRegistryService(
        private val severConnRegistry: ServerConnRegistry
    ) : RegistryService {

        private val log = getLogger(DefaultRegistryService::class.java)

        @Volatile
        private var currentConn: ServerConn = ServerConn.DEFAULT

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

        override fun setServerConn(remoteHost: String, remotePort: Int, localPort: Int) {
            val serverConn: ServerConn = ServerConn.create(remoteHost, remotePort, localPort)
            if (severConnRegistry.serverConn.compareAndSet(currentConn, serverConn)) {
                log.debug("ServerConn {}", serverConn)
                currentConn = serverConn
            }
        }

        override fun clearServerConn() {
            if (severConnRegistry.serverConn.compareAndSet(currentConn, ServerConn.DEFAULT)) {
                log.debug("Clear ServerConn {}", currentConn)
                currentConn = ServerConn.DEFAULT
            }
        }

        override fun getSeverConn(): ServerConn {
            return currentConn
        }

        override fun connect(remoteHost: String, remotePort: Int, localPort: Int) {
            connLock.lock()
            try {
                this.setServerConn(remoteHost, remotePort, localPort)
                this.markServerReady()
            } finally {
                connLock.unlock()
            }
        }

        override fun disconnect() {
            connLock.lock()
            try {
                this.clearServerConn()
                this.markServerNotReady()
            } finally {
                connLock.unlock()
            }
        }
    }
}