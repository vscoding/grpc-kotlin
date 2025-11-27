package io.intellij.kotlin.grpc.server.tasks.clear

import io.intellij.kotlin.grpc.commons.task.AbstractCronTask
import io.intellij.kotlin.grpc.server.context.RegistryService
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service

/**
 * ClearHistoryTask
 *
 * @author tech@intellij.io
 */
@Service
class ClearHistoryTask(
    private val taskScheduler: TaskScheduler,
    private val registryService: RegistryService
) : AbstractCronTask() {

    override fun getTaskScheduler(): TaskScheduler {
        return this.taskScheduler
    }

    override fun cron(): String {
        // 每一分钟执行一次
        return "0 * * * * ?"
    }

    override fun getRunnable(): Runnable {
        return Runnable { registryService.clearHistoryClients() }
    }

}
