package io.intellij.kotlin.grpc.client.entities

/**
 * GrpcConvertor
 *
 * @author tech@intellij.io
 */
interface GrpcConvertor<To> {
    fun cast(): To
}
