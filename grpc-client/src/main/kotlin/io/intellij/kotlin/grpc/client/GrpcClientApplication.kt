package io.intellij.kotlin.grpc.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * GrpcClientApplication
 *
 * @author tech@intellij.io
 */
@SpringBootApplication
class GrpcClientApplication

fun main(args: Array<String>) {
    runApplication<GrpcClientApplication>(*args)
}
