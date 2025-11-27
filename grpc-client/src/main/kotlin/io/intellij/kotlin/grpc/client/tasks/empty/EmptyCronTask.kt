package io.intellij.kotlin.grpc.client.tasks.empty

import io.intellij.kotlin.grpc.commons.task.AbstractCronTask
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service

/**
 * EmptyCronTask
 *
 * @author tech@intellij.io
 */
@Service
class EmptyCronTask : AbstractCronTask() {
    override fun getTaskScheduler(): TaskScheduler? {
        return null
    }

    override fun getRunnable(): Runnable? {
        return null
    }
}
