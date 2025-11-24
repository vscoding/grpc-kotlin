package io.intellij.kotlin.grpc.client.tasks.hb

import io.intellij.kotlin.grpc.client.service.HeartBeatService
import io.intellij.kotlin.grpc.task.AbstractCronTask
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * HeartBeatCronTask
 *
 * @author tech@intellij.io
 */
@Service
class HeartBeatCronTask(
    private val taskScheduler: TaskScheduler,
    private val heartBeatService: HeartBeatService

) : AbstractCronTask() {
    override fun cron(): String {
        return "*/3 * * * * ?"
    }

    override fun startOnInitializing(): Boolean {
        return true
    }

    override fun getTaskScheduler(): TaskScheduler {
        return this.taskScheduler
    }

    override fun getRunnable(): Runnable {
        return Runnable { heartBeatService.doHeartBeat(UUID.randomUUID().toString()) }
    }
}
