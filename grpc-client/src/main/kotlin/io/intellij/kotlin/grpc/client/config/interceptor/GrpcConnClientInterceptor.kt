package io.intellij.kotlin.grpc.client.config.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import io.intellij.kotlin.grpc.client.context.RegistryService
import io.intellij.kotlin.grpc.commons.config.getLogger

/**
 * GrpcConnClientInterceptor
 *
 * @author tech@intellij.io
 */
class GrpcConnClientInterceptor(
  val registryService: RegistryService,
) : ClientInterceptor {
  companion object {
    private val log = getLogger(GrpcConnClientInterceptor::class.java)
  }

  override fun <ReqT, RespT> interceptCall(
    method: MethodDescriptor<ReqT?, RespT?>?,
    callOptions: CallOptions?, next: Channel,
  ): ClientCall<ReqT, RespT> {

    return object : SimpleForwardingClientCall<ReqT, RespT>(next.newCall<ReqT, RespT>(method, callOptions)) {
      override fun start(responseListener: Listener<RespT>, headers: Metadata?) {
        super.start(
          object : SimpleForwardingClientCallListener<RespT>(responseListener) {
            override fun onClose(status: Status, trailers: Metadata) {
              try {
                super.onClose(status, trailers)
              } finally {
                when (status.code) {
                  Status.Code.OK -> registryService.markServerReady()
                  Status.Code.UNAVAILABLE -> {
                    registryService.markServerNotReady()
                    log.warn("Grpc call unavailable. method={}", method?.fullMethodName)
                  }

                  else -> log.debug(
                    "Grpc call closed. method={}, status={}",
                    method?.fullMethodName,
                    status.code,
                  )
                }
              }
            }
          },
          headers,
        )
      }

      override fun sendMessage(message: ReqT) {
        // log.debug("GrpcConnectionClientInterceptor sendMessage");
        super.sendMessage(message)
      }

      override fun cancel(message: String?, cause: Throwable?) {
        try {
          super.cancel(message, cause)
        } finally {
          log.debug("Grpc call cancelled. cause={}", cause?.javaClass ?: "NULL")
        }
      }
    }
  }
}
