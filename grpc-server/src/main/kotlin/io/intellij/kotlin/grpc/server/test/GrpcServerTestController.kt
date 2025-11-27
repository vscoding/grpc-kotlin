package io.intellij.kotlin.grpc.server.test

import io.intellij.kotlin.grpc.commons.config.getLogger
import io.intellij.kotlin.grpc.server.context.ClientConn
import io.intellij.kotlin.grpc.server.context.GrpcServerApplicationContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * GrpcServerTestController
 *
 * @author tech@intellij.io
 */
@RestController
@RequestMapping
class GrpcServerTestController(
    val grpcServerApplicationContext: GrpcServerApplicationContext
) {

    @ControllerAdvice
    class GlobalExceptionHandler {
        private val log = getLogger(GlobalExceptionHandler::class.java)

        @ExceptionHandler(Exception::class)
        @ResponseBody
        fun handleException(e: Exception): ResponseEntity<MutableMap<String?, Any?>?> {
            log.error("handle exception {}", e.message)
            return ResponseEntity.ok(
                mutableMapOf(
                    "code" to 500,
                    "message" to e.message
                )
            )
        }
    }

    @GetMapping("/liveClients")
    fun liveClients(): List<ClientConn> {
        return grpcServerApplicationContext.liveClients()
    }

    @GetMapping("/historyClients")
    fun historyClients(): List<ClientConn> {
        return grpcServerApplicationContext.historyClients()
    }

}