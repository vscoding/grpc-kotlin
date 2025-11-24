package io.intellij.kotlin.grpc.client.test

import io.intellij.kotlin.grpc.client.config.anno.ServerReadyThen
import io.intellij.kotlin.grpc.client.config.getLogger
import io.intellij.kotlin.grpc.client.context.GrpcApplicationContext
import io.intellij.kotlin.grpc.client.context.ServerConn
import io.intellij.kotlin.grpc.client.entities.GreetReq
import io.intellij.kotlin.grpc.client.entities.GreetResp
import io.intellij.kotlin.grpc.client.service.HeartBeatService
import io.intellij.kotlin.grpc.client.service.TestService
import io.intellij.kotlin.grpc.task.CronTask
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * GrpcClientTestController
 *
 * @author tech@intellij.io
 */
@RestController
class GrpcClientTestController(
    private val grpcApplicationContext: GrpcApplicationContext,
    private val heartBeatService: HeartBeatService,
    private val testService: TestService,
    private val tasks: MutableList<CronTask>
) {

    @GetMapping("/")
    @ServerReadyThen
    fun grpc(): MutableMap<String, Any> {
        val grpc: String = testService.test(grpcApplicationContext.applicationName)
        return mutableMapOf(
            "test" to grpc,
            "serverReady" to serverReady(),
            "serverConn" to serverConn()
        )
    }

    @PostMapping("/greet")
    fun greet(@RequestBody req: GreetReq): GreetResp {
        return testService.greet(req)
    }

    @GetMapping("/serverReady")
    fun serverReady(): Boolean {
        return grpcApplicationContext.serverReady()
    }

    @GetMapping("/serverConn")
    fun serverConn(): ServerConn {
        return grpcApplicationContext.serverConn()
    }


    @GetMapping("/startTasks")
    fun startTasks(): MutableList<TaskStatus> {
        tasks.forEach { it.start() }
        return taskStatus()
    }

    @GetMapping("/stopTasks")
    fun stopTasks(): MutableList<TaskStatus> {
        tasks.forEach { it.stop() }
        return taskStatus()
    }

    @GetMapping("/tasks")
    fun taskStatus(): MutableList<TaskStatus> {
        return tasks.map { task ->
            TaskStatus(
                className = task::class.java.name,
                cron = task.cron(),
                running = task.running()
            )
        }.toMutableList()
    }

    data class TaskStatus(
        val className: String,
        val cron: String,
        val running: Boolean
    )

    @GetMapping("/heartBeat")
    fun heartBeat() {
        heartBeatService.doHeartBeat("heartBeat")
    }

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

}