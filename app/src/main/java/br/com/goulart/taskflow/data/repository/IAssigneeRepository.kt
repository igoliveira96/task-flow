package br.com.goulart.taskflow.data.repository

import br.com.goulart.taskflow.data.model.Assignee
import kotlinx.coroutines.flow.Flow

interface IAssigneeRepository {
    fun getAssigneesStream(): Flow<List<Assignee>>
    fun getAssigneeStream(id: Long): Flow<Assignee?>
    suspend fun insert(assignee: Assignee): Long
    suspend fun update(assignee: Assignee)
    suspend fun delete(assignee: Assignee)
}
