package br.com.goulart.taskflow.data.repository

import br.com.goulart.taskflow.data.local.datasource.ProjectLocalDataSource
import br.com.goulart.taskflow.data.mapper.asEntity
import br.com.goulart.taskflow.data.mapper.asExternalModel
import br.com.goulart.taskflow.data.model.Project
import kotlinx.coroutines.flow.map

class DefaultProjectRepository(
    private val localDataSource: ProjectLocalDataSource,
) : ProjectRepository {
    override fun getProjectsStream() =
        localDataSource.getProjectsStream().map { projects ->
            projects.map { it.asExternalModel() }
        }

    override fun getProjectStream(id: Long) =
        localDataSource.getProjectStream(id).map { it?.asExternalModel() }

    override suspend fun insert(project: Project) =
        localDataSource.insert(project.asEntity())

    override suspend fun update(project: Project) =
        localDataSource.update(project.asEntity())

    override suspend fun delete(project: Project) =
        localDataSource.delete(project.asEntity())
}
