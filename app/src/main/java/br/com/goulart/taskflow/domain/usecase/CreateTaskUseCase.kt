package br.com.goulart.taskflow.domain.usecase

import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.model.TaskStatus
import br.com.goulart.taskflow.data.repository.ITaskRepository
import br.com.goulart.taskflow.domain.model.TaskInputConstraints
import br.com.goulart.taskflow.domain.task.TaskCodeGenerator
import br.com.goulart.taskflow.domain.time.TimeProvider
import org.koin.core.annotation.Factory

@Factory
class CreateTaskUseCase(
    private val taskRepository: ITaskRepository,
    private val taskCodeGenerator: TaskCodeGenerator,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        projectId: Long,
        title: String,
        description: String,
        status: TaskStatus,
    ): Long {
        val normalizedTitle = title.trim()
        val normalizedDescription = description.trim()

        require(projectId > 0) { "Project id must be greater than zero" }
        require(normalizedTitle.isNotEmpty()) { "Task title cannot be blank" }
        require(normalizedTitle.length <= TaskInputConstraints.TITLE_MAX_LENGTH) {
            "Task title is too long"
        }
        require(normalizedDescription.length <= TaskInputConstraints.DESCRIPTION_MAX_LENGTH) {
            "Task description is too long"
        }

        val timestamp = timeProvider.currentTimeMillis()
        val position = taskRepository.getNextPosition(projectId, status)

        return taskRepository.insert(
            Task(
                code = taskCodeGenerator.nextCode(),
                projectId = projectId,
                assignee = null,
                title = normalizedTitle,
                description = normalizedDescription,
                status = status,
                position = position,
                createdAt = timestamp,
                updatedAt = timestamp,
            ),
        )
    }
}
