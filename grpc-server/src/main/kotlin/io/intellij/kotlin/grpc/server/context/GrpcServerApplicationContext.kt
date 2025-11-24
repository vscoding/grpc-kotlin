package io.intellij.kotlin.grpc.server.context

import io.intellij.kotlin.grpc.server.config.getLogger
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.lang.NonNull
import org.springframework.stereotype.Component

/**
 * GrpcServerApplicationContext
 *
 * @author tech@intellij.io
 */
interface GrpcServerApplicationContext : ApplicationContextAware {
    /**
     * Retrieves the Spring application context associated with the GrpcServerApplicationContext.
     *
     * @return The Spring application context.
     */
    fun getSpringApplicationContext(): ApplicationContext?

    /**
     * Retrieves the list of connected clients.
     *
     * @return A List of ClientConn objects representing the connected clients.
     */
    fun liveClients(): List<ClientConn>

    /**
     * Retrieves the list of historical clients.
     *
     * @return A List of ClientConn objects representing the historical clients.
     */
    fun historyClients(): List<ClientConn>

    @Component
    class DefaultGrpcServerApplicationContext(
        private val registryOperator: RegistryOperator
    ) : GrpcServerApplicationContext {
        private val log = getLogger(DefaultGrpcServerApplicationContext::class.java)
        private var applicationContext: ApplicationContext? = null

        @Throws(BeansException::class)
        override fun setApplicationContext(@NonNull applicationContext: ApplicationContext) {
            log.info("ApplicationContext Aware")
            this.applicationContext = applicationContext
        }

        override fun getSpringApplicationContext(): ApplicationContext? {
            return this.applicationContext
        }

        override fun liveClients(): List<ClientConn> {
            return registryOperator.getLiveClients()
        }

        override fun historyClients(): List<ClientConn> {
            return registryOperator.getHistoryClients()
        }
    }
}
