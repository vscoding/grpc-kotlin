package io.intellij.kotlin.grpc.server.context

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import io.intellij.kotlin.grpc.context.Address
import org.springframework.stereotype.Repository

/**
 * ClientConnRegistry
 *
 * @author tech@intellij.io
 */
@Repository
class ClientConnRegistry {
    private val _live: MutableMap<Address, ClientConn> = Maps.newConcurrentMap()
    val live: MutableMap<Address, ClientConn> get() = _live

    private val _history: MutableList<ClientConn> = Lists.newCopyOnWriteArrayList()
    val history: MutableList<ClientConn> get() = _history

    fun clearHistoryClients() = _history.clear()

}
