package br.com.goulart.taskflow.ui.home

sealed interface HomeAction {
    data object OpenCreateProjectDialog : HomeAction
    data object DismissCreateProjectDialog : HomeAction
    data class ProjectNameChanged(val value: String) : HomeAction
    data class ProjectDescriptionChanged(val value: String) : HomeAction
    data class ProjectSelected(val id: Long) : HomeAction
    data object CreateProject : HomeAction
}
