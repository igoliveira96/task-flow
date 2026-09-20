package br.com.goulart.taskflow.domain.usecase

import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.model.TaskStatus
import br.com.goulart.taskflow.data.repository.ITaskRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateTaskStatusUseCase(
    private val iTaskRepository: ITaskRepository,
) {
    suspend operator fun invoke(
        task: Task,
        status: TaskStatus,
    ) {
        require(task.id > 0) { "Task id must be greater than zero" }
        require(task.projectId > 0) { "Project id must be greater than zero" }
        if (task.status == status) return

        val nextPosition = iTaskRepository.getNextPosition(task.projectId, status)
        iTaskRepository.updateStatus(task.id, status, nextPosition)
    }
}
