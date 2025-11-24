package io.intellij.kotlin.grpc.client.service

import io.intellij.kotlin.grpc.api.HelloServiceGrpc
import io.intellij.kotlin.grpc.api.TestProto
import io.intellij.kotlin.grpc.client.config.GrpcConfig
import io.intellij.kotlin.grpc.client.entities.GreetReq
import io.intellij.kotlin.grpc.client.entities.GreetResp
import io.intellij.kotlin.grpc.client.entities.GrpcConvertUtils
import io.intellij.kotlin.grpc.multi.MultiServiceGrpc
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service

/**
 * TestService
 *
 * @author tech@intellij.io
 */
interface TestService {
    /**
     * Performs a test operation using the given name.
     *
     * @param name the name to be used for testing
     * @return the result of the test operation as a string
     */
    fun test(name: String?): String

    /**
     * Performs a greet operation using the given GreetReq object.
     *
     * @param req the GreetReq object containing the necessary parameters for the greet operation
     * @return the GreetResp object containing the greeting
     */
    fun greet(req: GreetReq): GreetResp

    @Service
    class DefaultTestService : TestService {

        @GrpcClient(GrpcConfig.GRPC_SERVER_INSTANCE)
        private lateinit var helloServiceBlockingStub: HelloServiceGrpc.HelloServiceBlockingStub

        @GrpcClient(GrpcConfig.GRPC_SERVER_INSTANCE)
        private lateinit var multiServiceBlockingStub: MultiServiceGrpc.MultiServiceBlockingStub

        override fun test(name: String?): String {
            val helloResponse: TestProto.HelloResponse = helloServiceBlockingStub.test(
                TestProto.HelloRequest.newBuilder().setName(name).build()
            )
            return helloResponse.getWelcome()
        }

        override fun greet(req: GreetReq): GreetResp {
            return GrpcConvertUtils.convert(
                multiServiceBlockingStub.sayHello(
                    req.convert()
                )
            )
        }
    }
}
