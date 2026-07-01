package io.intellij.kotlin.grpc.client.entities

/**
 * GrpcConvertor
 *
 * @author dev@intellij.io
 */
interface GrpcConvertor<To> {
  fun cast(): To
}
