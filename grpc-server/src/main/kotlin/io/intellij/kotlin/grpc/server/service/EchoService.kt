package io.intellij.kotlin.grpc.server.service

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.EchoProto
import io.intellij.kotlin.grpc.api.HelloServiceGrpc
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService

/**
 * TestService
 *
 * @author tech@intellij.io
 */
@GrpcService
class EchoService : HelloServiceGrpc.HelloServiceImplBase() {
    private val log = getLogger(EchoService::class.java)

    override fun greeting(
        request: EchoProto.HelloRequest,
        responseObserver: StreamObserver<EchoProto.HelloResponse>
    ) {
        val name = request.name
        log.debug("receive name is {}", name)
        responseObserver.onNext(EchoProto.HelloResponse.newBuilder().setWelcome("Hello,$name").build())
        responseObserver.onCompleted()
    }
}
