package br.com.goulart.taskflow.data.model

data class Task(
    val id: Long = 0,
    val code: String,
    val projectId: Long,
    val assignee: Assignee?,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val position: Int,
    val createdAt: Long,
    val updatedAt: Long,
)
