package io.intellij.kotlin.grpc.client.config.anno

/**
 * RequireGrpcServerReady
 *
 * @author tech@intellij.io
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class RequireGrpcServerReady
