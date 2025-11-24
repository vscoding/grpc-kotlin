package io.intellij.kotlin.grpc.client.expection

/**
 * ServerNotReadyException
 *
 * @author tech@intellij.io
 */
class ServerNotReadyException private constructor() : RuntimeException(MSG) {
    companion object {
        private const val MSG = "Grpc Server Not Ready"

        @JvmStatic
        fun create(): ServerNotReadyException = ServerNotReadyException()
    }
}