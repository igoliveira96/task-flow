package br.com.goulart.taskflow.data.model

data class Project(
    val id: Long = 0,
    val name: String,
    val description: String,
    val createdAt: Long,
)
