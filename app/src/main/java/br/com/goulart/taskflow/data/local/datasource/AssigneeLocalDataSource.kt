package br.com.goulart.taskflow.data.local.datasource

import br.com.goulart.taskflow.data.local.dao.AssigneeDao
import br.com.goulart.taskflow.data.local.entity.AssigneeEntity
import kotlinx.coroutines.flow.Flow

interface AssigneeLocalDataSource {
    fun getAssigneesStream(): Flow<List<AssigneeEntity>>
    fun getAssigneeStream(id: Long): Flow<AssigneeEntity?>
    suspend fun insert(assignee: AssigneeEntity): Long
    suspend fun update(assignee: AssigneeEntity)
    suspend fun delete(assignee: AssigneeEntity)
}

class RoomAssigneeLocalDataSource(
    private val assigneeDao: AssigneeDao,
) : AssigneeLocalDataSource {
    override fun getAssigneesStream() = assigneeDao.getAssigneesStream()

    override fun getAssigneeStream(id: Long) = assigneeDao.getAssigneeStream(id)

    override suspend fun insert(assignee: AssigneeEntity) = assigneeDao.insert(assignee)

    override suspend fun update(assignee: AssigneeEntity) = assigneeDao.update(assignee)

    override suspend fun delete(assignee: AssigneeEntity) = assigneeDao.delete(assignee)
}
