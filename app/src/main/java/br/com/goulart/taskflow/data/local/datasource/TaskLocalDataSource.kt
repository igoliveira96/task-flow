package br.com.goulart.taskflow.data.local.datasource

import br.com.goulart.taskflow.data.local.dao.TaskDao
import br.com.goulart.taskflow.data.local.entity.TaskEntity
import br.com.goulart.taskflow.data.local.relation.TaskWithAssignee
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

interface TaskLocalDataSource {
    fun getTasksWithAssigneeStream(projectId: Long): Flow<List<TaskWithAssignee>>
    fun getTaskWithAssigneeStream(taskId: Long): Flow<TaskWithAssignee?>
    suspend fun insert(task: TaskEntity): Long
    suspend fun update(task: TaskEntity)
    suspend fun delete(task: TaskEntity)
    suspend fun getNextPosition(projectId: Long, status: String): Int
    suspend fun updateStatus(taskId: Long, status: String, position: Int, updatedAt: Long)
}

@Single
class RoomTaskLocalDataSource(
    private val taskDao: TaskDao,
) : TaskLocalDataSource {
    override fun getTasksWithAssigneeStream(projectId: Long) =
        taskDao.getTasksWithAssigneeStream(projectId)

    override fun getTaskWithAssigneeStream(taskId: Long) =
        taskDao.getTaskWithAssigneeStream(taskId)

    override suspend fun insert(task: TaskEntity) = taskDao.insert(task)

    override suspend fun update(task: TaskEntity) = taskDao.update(task)

    override suspend fun delete(task: TaskEntity) = taskDao.delete(task)

    override suspend fun getNextPosition(projectId: Long, status: String) =
        taskDao.getNextPosition(projectId, status)

    override suspend fun updateStatus(
        taskId: Long,
        status: String,
        position: Int,
        updatedAt: Long,
    ) = taskDao.updateStatus(taskId, status, position, updatedAt)
}
