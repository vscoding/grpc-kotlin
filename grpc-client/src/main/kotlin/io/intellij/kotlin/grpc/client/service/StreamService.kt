package io.intellij.kotlin.grpc.client.service

import io.grpc.stub.ClientCallStreamObserver
import io.grpc.stub.ClientResponseObserver
import io.grpc.stub.StreamObserver
import io.intellij.kotlin.grpc.api.BidiStreamServiceGrpc
import io.intellij.kotlin.grpc.api.ClientStreamServiceGrpc
import io.intellij.kotlin.grpc.api.ServerStreamServiceGrpc
import io.intellij.kotlin.grpc.api.stream.StreamRequest
import io.intellij.kotlin.grpc.api.stream.StreamResponse
import io.intellij.kotlin.grpc.client.config.GrpcConfig
import io.intellij.kotlin.grpc.commons.config.getLogger
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

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
  private val taskExecutor: TaskExecutor,
) : StreamService {

  companion object {
    private val log = getLogger(StreamService::class.java)
  }

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
    val next = AtomicInteger(1)
    val completed = AtomicBoolean(false)

    clientStreamServiceStub.withDeadlineAfter(10, TimeUnit.SECONDS).clientStreaming(
      object : ClientResponseObserver<StreamRequest, StreamResponse> {
        private lateinit var requestStream: ClientCallStreamObserver<StreamRequest>

        override fun beforeStart(requestStream: ClientCallStreamObserver<StreamRequest>) {
          this.requestStream = requestStream
          requestStream.setOnReadyHandler {
            drainRequests(requestStream, next, count, "[Client Stream] Request Data:", completed)
          }
          taskExecutor.execute {
            drainRequests(requestStream, next, count, "[Client Stream] Request Data:", completed)
          }
        }

        override fun onNext(response: StreamResponse) {
          log.info("[Client Stream] Received response from server: {}", response.data)
        }

        override fun onError(t: Throwable) {
          completed.set(true)
          log.error("Error occurred during client streaming", t)
        }

        override fun onCompleted() {
          completed.set(true)
          log.info("[Client Stream] completed")
        }
      },
    )
  }

  override fun serverStream(data: String) {
    val request = StreamRequest.newBuilder().setData(data).build()
    serverStreamServiceStub.withDeadlineAfter(10, TimeUnit.SECONDS).serverStreaming(
      request,
      object : StreamObserver<StreamResponse> {
        private var responseCount = 0

        override fun onNext(response: StreamResponse) {
          response.data.also { respData ->
            responseCount++
            log.info("[Server Stream] Received response from server: {}", respData)
          }
        }

        override fun onError(t: Throwable) {
          log.error("Error occurred during server streaming", t)
        }

        override fun onCompleted() {
          log.info("[Server Stream] completed. response count={}", responseCount)
        }
      },
    )
  }

  override fun bidiStream(count: Int) {
    val next = AtomicInteger(1)
    val completed = AtomicBoolean(false)

    bidiStreamServiceGrpcStub.withDeadlineAfter(10, TimeUnit.SECONDS).bidiStreaming(
      object : ClientResponseObserver<StreamRequest, StreamResponse> {
        private lateinit var requestStream: ClientCallStreamObserver<StreamRequest>

        override fun beforeStart(requestStream: ClientCallStreamObserver<StreamRequest>) {
          this.requestStream = requestStream
          requestStream.setOnReadyHandler {
            drainRequests(requestStream, next, count, "[Bidi Stream] Request Data:", completed)
          }
          taskExecutor.execute {
            drainRequests(requestStream, next, count, "[Bidi Stream] Request Data:", completed)
          }
        }

        override fun onNext(response: StreamResponse) {
          response.data.also { respData ->
            log.info("[Bidi Stream] Received response from server: {}", respData)
          }
        }

        override fun onError(t: Throwable) {
          completed.set(true)
          log.error("Error occurred during bidi streaming", t)
        }

        override fun onCompleted() {
          completed.set(true)
          log.info("[Bidi Stream] completed")
        }

      },
    )

  }

  private fun drainRequests(
    requestStream: ClientCallStreamObserver<StreamRequest>,
    next: AtomicInteger,
    count: Int,
    prefix: String,
    completed: AtomicBoolean,
  ) {
    synchronized(requestStream) {
      while (!completed.get() && requestStream.isReady && next.get() <= count) {
        val index = next.getAndIncrement()
        requestStream.onNext(StreamRequest.newBuilder().setData("$prefix $index").build())
      }
      if (next.get() > count && completed.compareAndSet(false, true)) {
        requestStream.onCompleted()
      }
    }
  }

}
