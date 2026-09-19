package br.com.goulart.taskflow.data.repository

import br.com.goulart.taskflow.data.local.datasource.TaskLocalDataSource
import br.com.goulart.taskflow.data.mapper.asEntity
import br.com.goulart.taskflow.data.mapper.asExternalModel
import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.model.TaskStatus
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val localDataSource: TaskLocalDataSource,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) : ITaskRepository {
    override fun getTasksStream(projectId: Long) =
        localDataSource.getTasksWithAssigneeStream(projectId).map { tasks ->
            tasks.map { it.asExternalModel() }
        }

    override fun getTaskStream(taskId: Long) =
        localDataSource.getTaskWithAssigneeStream(taskId).map { it?.asExternalModel() }

    override suspend fun insert(task: Task) =
        localDataSource.insert(task.asEntity())

    override suspend fun update(task: Task) =
        localDataSource.update(
            task.copy(updatedAt = currentTimeMillis()).asEntity(),
        )

    override suspend fun delete(task: Task) =
        localDataSource.delete(task.asEntity())

    override suspend fun updateStatus(
        taskId: Long,
        status: TaskStatus,
        position: Int,
    ) = localDataSource.updateStatus(
        taskId = taskId,
        status = status.storageValue,
        position = position,
        updatedAt = currentTimeMillis(),
    )
}
