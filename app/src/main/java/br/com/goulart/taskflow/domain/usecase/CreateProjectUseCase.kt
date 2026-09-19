package br.com.goulart.taskflow.domain.usecase

import br.com.goulart.taskflow.data.model.Project
import br.com.goulart.taskflow.data.repository.IProjectRepository
import br.com.goulart.taskflow.domain.model.ProjectInputConstraints
import br.com.goulart.taskflow.domain.time.TimeProvider
import org.koin.core.annotation.Factory

@Factory
class CreateProjectUseCase(
    private val projectRepository: IProjectRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        name: String,
        description: String,
    ): Long {
        val normalizedName = name.trim()
        val normalizedDescription = description.trim()

        require(normalizedName.isNotEmpty()) { "Project name cannot be blank" }
        require(normalizedName.length <= ProjectInputConstraints.NAME_MAX_LENGTH) {
            "Project name is too long"
        }
        require(normalizedDescription.length <= ProjectInputConstraints.DESCRIPTION_MAX_LENGTH) {
            "Project description is too long"
        }

        return projectRepository.insert(
            Project(
                name = normalizedName,
                description = normalizedDescription,
                createdAt = timeProvider.currentTimeMillis(),
            ),
        )
    }
}
