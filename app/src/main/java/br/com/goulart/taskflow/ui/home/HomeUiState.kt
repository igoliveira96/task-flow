package br.com.goulart.taskflow.ui.home

import br.com.goulart.taskflow.data.model.Project
import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.model.TaskStatus

data class HomeUiState(
    val projects: List<Project> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val selectedProjectId: Long? = null,
    val isCreateProjectDialogVisible: Boolean = false,
    val createProjectForm: CreateProjectFormState = CreateProjectFormState(),
    val isCreateTaskDialogVisible: Boolean = false,
    val createTaskForm: CreateTaskFormState = CreateTaskFormState(),
) {
    val selectedProject: Project?
        get() = projects.firstOrNull { it.id == selectedProjectId }
}

data class CreateTaskFormState(
    val title: String = "",
    val description: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val titleError: TaskTitleError? = null,
    val descriptionError: TaskDescriptionError? = null,
    val isSaving: Boolean = false,
    val submitError: TaskSubmitError? = null,
) {
    val canSubmit: Boolean
        get() = title.isNotBlank() &&
            titleError == null &&
            descriptionError == null &&
            !isSaving
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

enum class TaskTitleError {
    REQUIRED,
    TOO_LONG,
}

enum class TaskDescriptionError {
    TOO_LONG,
}

enum class TaskSubmitError {
    UNKNOWN,
}
