package br.com.goulart.taskflow.domain.usecase

import br.com.goulart.taskflow.data.model.Project
import br.com.goulart.taskflow.data.repository.IProjectRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveProjectsUseCase(
    private val projectRepository: IProjectRepository,
) {
    operator fun invoke(): Flow<List<Project>> = projectRepository.getProjectsStream()
}
