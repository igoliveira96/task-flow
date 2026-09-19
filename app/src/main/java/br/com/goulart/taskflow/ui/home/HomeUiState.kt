package br.com.goulart.taskflow.ui.home

import br.com.goulart.taskflow.data.model.Project

data class HomeUiState(
    val projects: List<Project> = emptyList(),
    val selectedProjectId: Long? = null,
    val isCreateProjectDialogVisible: Boolean = false,
    val createProjectForm: CreateProjectFormState = CreateProjectFormState(),
) {
    val selectedProject: Project?
        get() = projects.firstOrNull { it.id == selectedProjectId }
}

data class CreateProjectFormState(
    val name: String = "",
    val description: String = "",
    val nameError: ProjectNameError? = null,
    val descriptionError: ProjectDescriptionError? = null,
    val isSaving: Boolean = false,
    val submitError: ProjectSubmitError? = null,
) {
    val canSubmit: Boolean
        get() = name.isNotBlank() &&
            nameError == null &&
            descriptionError == null &&
            !isSaving
}

enum class ProjectNameError {
    REQUIRED,
    TOO_LONG,
}

enum class ProjectDescriptionError {
    TOO_LONG,
}

enum class ProjectSubmitError {
    UNKNOWN,
}
