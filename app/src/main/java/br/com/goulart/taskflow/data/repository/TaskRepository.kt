package br.com.goulart.taskflow.data.repository

import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksStream(projectId: Long): Flow<List<Task>>
    fun getTaskStream(taskId: Long): Flow<Task?>
    suspend fun insert(task: Task): Long
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    suspend fun updateStatus(taskId: Long, status: TaskStatus, position: Int)
}
