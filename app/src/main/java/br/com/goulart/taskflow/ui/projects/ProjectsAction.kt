package br.com.goulart.taskflow.ui.projects

sealed interface ProjectsAction {
    data class OpenEditProject(val projectId: Long) : ProjectsAction
    data object DismissEditProject : ProjectsAction
    data class ProjectNameChanged(val value: String) : ProjectsAction
    data class ProjectDescriptionChanged(val value: String) : ProjectsAction
    data object SaveProject : ProjectsAction
}
