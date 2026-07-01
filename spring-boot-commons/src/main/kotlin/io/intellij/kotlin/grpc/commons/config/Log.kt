package io.intellij.kotlin.grpc.commons.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Log
 *
 *
 * @author dev@intellij.io
 * @since 2021/1/5
 */

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)

/*
abstract class Log {
    val log: Logger = LoggerFactory.io.intellij.kotlin.grpc.commons.config.getLogger(this.javaClass)
}
*/