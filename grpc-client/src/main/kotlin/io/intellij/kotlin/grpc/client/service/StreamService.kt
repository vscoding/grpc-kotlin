package io.intellij.kotlin.grpc.client.service

import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.BidiStreamServiceGrpc
import io.intellij.kotlin.grpc.api.ClientStreamServiceGrpc
import io.intellij.kotlin.grpc.api.ServerStreamServiceGrpc
import io.intellij.kotlin.grpc.api.stream.StreamRequest
import io.intellij.kotlin.grpc.api.stream.StreamResponse
import io.intellij.kotlin.grpc.client.config.GrpcConfig
import io.intellij.kotlin.grpc.client.config.getLogger
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import java.lang.Thread.sleep

/**
 * StreamService
 *
 * @author tech@intellij.io
 */
interface StreamService {

    @kotlin.jvm.Throws(Exception::class)
    fun clientStream(count: Int)

    @kotlin.jvm.Throws(Exception::class)
    fun serverStream(data: String)

    @kotlin.jvm.Throws(Exception::class)
    fun bidiStream(count: Int)

}

@Service
class DefaultStreamService(
    private val taskExecutor: TaskExecutor
) : StreamService {

    private val log = getLogger(DefaultStreamService::class.java)

    @GrpcClient(GrpcConfig.GRPC_SERVER_INSTANCE)
    private lateinit var clientStreamServiceStub: ClientStreamServiceGrpc.ClientStreamServiceStub

    @GrpcClient(GrpcConfig.GRPC_SERVER_INSTANCE)
    private lateinit var serverStreamServiceStub: ServerStreamServiceGrpc.ServerStreamServiceStub

    @GrpcClient(GrpcConfig.GRPC_SERVER_INSTANCE)
    private lateinit var bidiStreamServiceGrpcStub: BidiStreamServiceGrpc.BidiStreamServiceStub

    /**
     * Handles communication or operations between client and server using a streaming service.
     *
     * @param count the number of messages or elements to be sent through the client-to-server stream
     */
    override fun clientStream(count: Int) {

        val requestObserver: StreamObserver<StreamRequest> =
            clientStreamServiceStub.clientStreaming(object : StreamObserver<StreamResponse> {
                override fun onNext(response: StreamResponse) {
                    log.info("[Client Stream] Received response from server: {}", response.data)
                }

                override fun onError(t: Throwable) {
                    log.error("Error occurred during client streaming", t)
                }

                override fun onCompleted() {
                    log.info("[Client Stream] completed")
                }
            })

        for (i in 1..count) {
            requestObserver.onNext(
                StreamRequest.newBuilder().setData("[Client Stream] Request Data: $i").build()
            )
            sleep(100)
        }

        requestObserver.onCompleted()

    }

    override fun serverStream(data: String) {
        val request = StreamRequest.newBuilder().setData(data).build()
        serverStreamServiceStub.serverStreaming(request, object : StreamObserver<StreamResponse> {
            private val dataList = mutableListOf<String>()

            override fun onNext(response: StreamResponse) {
                response.data.let { respData ->
                    log.info("[Server Stream] Received response from server: {}", respData)
                    dataList.add(respData)
                }
            }

            override fun onError(t: Throwable) {
                log.error("Error occurred during server streaming", t)
            }

            override fun onCompleted() {
                log.info("[Server Stream] completed")
                dataList.forEach { dataItem ->
                    log.info("Collected data item: {}", dataItem)
                }
            }
        })
    }

    override fun bidiStream(count: Int) {

        bidiStreamServiceGrpcStub.bidiStreaming(object : StreamObserver<StreamResponse> {
            override fun onNext(response: StreamResponse) {
                response.data.let { respData ->
                    taskExecutor.execute { log.info("[Bidi Stream] Received response from server: {}", respData) }
                }
            }

            override fun onError(t: Throwable) {
                log.error("Error occurred during bidi streaming", t)
            }

            override fun onCompleted() {
                log.info("[Bidi Stream] completed")
            }

        }).let { requestObserver ->
            for (i in 1..count) {
                requestObserver.onNext(StreamRequest.newBuilder().setData("[Bidi Stream] Request Data: $i").build())
                sleep(100)
            }
            requestObserver.onCompleted()
        }

    }

}
