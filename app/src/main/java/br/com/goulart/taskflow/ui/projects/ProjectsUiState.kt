package br.com.goulart.taskflow.ui.projects

import br.com.goulart.taskflow.data.model.Project

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val editingProjectId: Long? = null,
    val editForm: EditProjectFormState = EditProjectFormState(),
) {
    val editingProject: Project?
        get() = projects.firstOrNull { it.id == editingProjectId }
}

data class EditProjectFormState(
    val name: String = "",
    val description: String = "",
    val nameError: EditProjectNameError? = null,
    val descriptionError: EditProjectDescriptionError? = null,
    val isSaving: Boolean = false,
    val submitError: EditProjectSubmitError? = null,
) {
    val canSubmit: Boolean
        get() = name.isNotBlank() &&
            nameError == null &&
            descriptionError == null &&
            !isSaving
}

enum class EditProjectNameError {
    REQUIRED,
    TOO_LONG,
}

enum class EditProjectDescriptionError {
    TOO_LONG,
}

enum class EditProjectSubmitError {
    UNKNOWN,
}
