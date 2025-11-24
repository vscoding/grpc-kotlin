package io.intellij.kotlin.grpc.server.tasks.print

import io.intellij.kotlin.grpc.server.config.getLogger
import io.intellij.kotlin.grpc.server.context.GrpcServerApplicationContext
import io.intellij.kotlin.grpc.task.AbstractCronTask
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service

/**
 * ClientConnPrintTask
 *
 * @author tech@intellij.io
 */
@Service
class ClientConnPrintTask(
    private val taskScheduler: TaskScheduler,
    private val grpcServerApplicationContext: GrpcServerApplicationContext
) : AbstractCronTask() {
    private val log = getLogger(ClientConnPrintTask::class.java)

    override fun startOnInitializing(): Boolean {
        return true
    }

    override fun getTaskScheduler(): TaskScheduler {
        return this.taskScheduler
    }

    override fun getRunnable(): Runnable {
        return Runnable {
            log.debug("List Client's Connections .START")
            grpcServerApplicationContext.liveClients().forEach {
                log.debug("up client = {}", it)
            }
            log.debug("List Client's Connections .END")
        }
    }

}
