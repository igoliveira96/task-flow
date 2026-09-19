package br.com.goulart.taskflow.domain.usecase

import br.com.goulart.taskflow.data.model.Project
import br.com.goulart.taskflow.data.repository.IProjectRepository
import br.com.goulart.taskflow.domain.model.ProjectInputConstraints
import org.koin.core.annotation.Factory

@Factory
class UpdateProjectUseCase(
    private val projectRepository: IProjectRepository,
) {
    suspend operator fun invoke(
        project: Project,
        name: String,
        description: String,
    ) {
        val normalizedName = name.trim()
        val normalizedDescription = description.trim()

        require(project.id > 0) { "Project id must be greater than zero" }
        require(normalizedName.isNotEmpty()) { "Project name cannot be blank" }
        require(normalizedName.length <= ProjectInputConstraints.NAME_MAX_LENGTH) {
            "Project name is too long"
        }
        require(normalizedDescription.length <= ProjectInputConstraints.DESCRIPTION_MAX_LENGTH) {
            "Project description is too long"
        }

        projectRepository.update(
            project.copy(
                name = normalizedName,
                description = normalizedDescription,
            ),
        )
    }
}
