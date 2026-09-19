package br.com.goulart.taskflow.domain.usecase

import br.com.goulart.taskflow.data.model.TaskStatus
import br.com.goulart.taskflow.data.repository.ITaskRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateTaskStatusUseCase(
    private val iTaskRepository: ITaskRepository,
) {
    suspend operator fun invoke(
        taskId: Long,
        status: TaskStatus,
        position: Int,
    ) {
        require(taskId > 0) { "Task id must be greater than zero" }
        require(position >= 0) { "Task position cannot be negative" }
        iTaskRepository.updateStatus(taskId, status, position)
    }
}
