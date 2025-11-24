package io.intellij.kotlin.grpc.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * GrpcServerApplication
 *
 * @author tech@intellij.io
 */
@SpringBootApplication
class GrpcServerApplication

fun main(args: Array<String>) {
    runApplication<GrpcServerApplication>(*args)
}