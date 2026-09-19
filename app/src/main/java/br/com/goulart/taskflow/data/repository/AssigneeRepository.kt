package br.com.goulart.taskflow.data.repository

import br.com.goulart.taskflow.data.local.datasource.AssigneeLocalDataSource
import br.com.goulart.taskflow.data.mapper.asEntity
import br.com.goulart.taskflow.data.mapper.asExternalModel
import br.com.goulart.taskflow.data.model.Assignee
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class AssigneeRepository(
    private val localDataSource: AssigneeLocalDataSource,
) : IAssigneeRepository {
    override fun getAssigneesStream() =
        localDataSource.getAssigneesStream().map { assignees ->
            assignees.map { it.asExternalModel() }
        }

    override fun getAssigneeStream(id: Long) =
        localDataSource.getAssigneeStream(id).map { it?.asExternalModel() }

    override suspend fun insert(assignee: Assignee) =
        localDataSource.insert(assignee.asEntity())

    override suspend fun update(assignee: Assignee) =
        localDataSource.update(assignee.asEntity())

    override suspend fun delete(assignee: Assignee) =
        localDataSource.delete(assignee.asEntity())
}
