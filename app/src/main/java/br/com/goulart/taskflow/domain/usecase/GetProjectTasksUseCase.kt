package br.com.goulart.taskflow.domain.usecase

import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.repository.ITaskRepository
import kotlinx.coroutines.flow.Flow

class GetProjectTasksUseCase(
    private val iTaskRepository: ITaskRepository,
) {
    operator fun invoke(projectId: Long): Flow<List<Task>> {
        require(projectId > 0) { "Project id must be greater than zero" }
        return iTaskRepository.getTasksStream(projectId)
    }
}
