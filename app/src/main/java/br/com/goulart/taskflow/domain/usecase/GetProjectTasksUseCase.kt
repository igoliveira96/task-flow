package br.com.goulart.taskflow.domain.usecase

import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetProjectTasksUseCase(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(projectId: Long): Flow<List<Task>> {
        require(projectId > 0) { "Project id must be greater than zero" }
        return taskRepository.getTasksStream(projectId)
    }
}
