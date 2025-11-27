package io.intellij.kotlin.grpc.server.service

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.multi.Gender
import io.intellij.kotlin.grpc.multi.GreetRequest
import io.intellij.kotlin.grpc.multi.GreetResponse
import io.intellij.kotlin.grpc.multi.MultiServiceGrpc
import net.devh.boot.grpc.server.service.GrpcService

/**
 * MultiService
 *
 * @author tech@intellij.io
 */
@GrpcService
class MultiService : MultiServiceGrpc.MultiServiceImplBase() {
    private val log = getLogger(MultiService::class.java)

    override fun sayHello(request: GreetRequest, responseObserver: StreamObserver<GreetResponse>) {
        val id: Int = request.id
        val name: String = request.getName()
        val gender: Gender = request.getGender()
        val emails: List<String> = request.emailsList.toList()
        log.info("id: {}, name: {}, gender: {}, emails: {}", id, name, gender, emails)

        responseObserver.onNext(
            GreetResponse.newBuilder().setMsg("Hello,$name").build()
        )

        responseObserver.onCompleted()
    }
}
