package br.com.goulart.taskflow.ui.home

import br.com.goulart.taskflow.data.model.TaskStatus

sealed interface HomeAction {
    data object OpenCreateProjectDialog : HomeAction
    data object DismissCreateProjectDialog : HomeAction
    data class ProjectNameChanged(val value: String) : HomeAction
    data class ProjectDescriptionChanged(val value: String) : HomeAction
    data class ProjectSelected(val id: Long) : HomeAction
    data object CreateProject : HomeAction
    data object OpenCreateTaskDialog : HomeAction
    data object DismissCreateTaskDialog : HomeAction
    data class TaskTitleChanged(val value: String) : HomeAction
    data class TaskDescriptionChanged(val value: String) : HomeAction
    data class TaskStatusChanged(val value: TaskStatus) : HomeAction
    data object CreateTask : HomeAction
    data class TaskMoved(val taskId: Long, val destination: TaskStatus) : HomeAction
    data object DismissTaskMoveError : HomeAction
}
