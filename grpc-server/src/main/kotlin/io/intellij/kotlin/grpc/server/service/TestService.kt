package io.intellij.kotlin.grpc.server.service

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.HelloServiceGrpc
import io.intellij.kotlin.grpc.api.TestProto
import io.intellij.kotlin.grpc.server.config.getLogger
import net.devh.boot.grpc.server.service.GrpcService

/**
 * TestService
 *
 * @author tech@intellij.io
 */
@GrpcService
class TestService : HelloServiceGrpc.HelloServiceImplBase() {
    private val log = getLogger(TestService::class.java)

    override fun test(request: TestProto.HelloRequest, responseObserver: StreamObserver<TestProto.HelloResponse>) {
        val name: String? = request.getName()
        log.debug("receive name is {}", name)
        responseObserver.onNext(
            TestProto.HelloResponse.newBuilder()
                .setWelcome("Hello,$name")
                .build()
        )
        responseObserver.onCompleted()
    }

}
