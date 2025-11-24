package io.intellij.kotlin.grpc.client.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Log
 *
 *
 * @author tech@intellij.io
 * @since 2021/1/5
 */

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

/*
abstract class Log {
    val log: Logger = LoggerFactory.io.intellij.kotlin.grpc.client.config.getLogger(this.javaClass)
}
*/
