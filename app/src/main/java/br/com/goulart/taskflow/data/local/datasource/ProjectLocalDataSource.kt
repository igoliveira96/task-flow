package br.com.goulart.taskflow.data.local.datasource

import br.com.goulart.taskflow.data.local.dao.ProjectDao
import br.com.goulart.taskflow.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

interface ProjectLocalDataSource {
    fun getProjectsStream(): Flow<List<ProjectEntity>>
    fun getProjectStream(id: Long): Flow<ProjectEntity?>
    suspend fun insert(project: ProjectEntity): Long
    suspend fun update(project: ProjectEntity)
    suspend fun delete(project: ProjectEntity)
}

@Single
class RoomProjectLocalDataSource(
    private val projectDao: ProjectDao,
) : ProjectLocalDataSource {
    override fun getProjectsStream() = projectDao.getProjectsStream()

    override fun getProjectStream(id: Long) = projectDao.getProjectStream(id)

    override suspend fun insert(project: ProjectEntity) = projectDao.insert(project)

    override suspend fun update(project: ProjectEntity) = projectDao.update(project)

    override suspend fun delete(project: ProjectEntity) = projectDao.delete(project)
}
