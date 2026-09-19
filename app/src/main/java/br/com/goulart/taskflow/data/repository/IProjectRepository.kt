package br.com.goulart.taskflow.data.repository

import br.com.goulart.taskflow.data.model.Project
import kotlinx.coroutines.flow.Flow

interface IProjectRepository {
    fun getProjectsStream(): Flow<List<Project>>
    fun getProjectStream(id: Long): Flow<Project?>
    suspend fun insert(project: Project): Long
    suspend fun update(project: Project)
    suspend fun delete(project: Project)
}
