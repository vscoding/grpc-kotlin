package io.intellij.kotlin.grpc.commons.task

/**
 * TaskStatus
 *
 * @author tech@intellij.io
 */
data class TaskStatus(
    val className: String,
    val cron: String,
    val running: Boolean
)
