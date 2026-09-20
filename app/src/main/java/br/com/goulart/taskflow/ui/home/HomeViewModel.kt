package br.com.goulart.taskflow.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.model.TaskStatus
import br.com.goulart.taskflow.domain.model.ProjectInputConstraints
import br.com.goulart.taskflow.domain.model.TaskInputConstraints
import br.com.goulart.taskflow.domain.usecase.CreateProjectUseCase
import br.com.goulart.taskflow.domain.usecase.CreateTaskUseCase
import br.com.goulart.taskflow.domain.usecase.GetProjectTasksUseCase
import br.com.goulart.taskflow.domain.usecase.ObserveProjectsUseCase
import br.com.goulart.taskflow.domain.usecase.UpdateTaskStatusUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    observeProjectsUseCase: ObserveProjectsUseCase,
    getProjectTasksUseCase: GetProjectTasksUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val interactionState = MutableStateFlow(
        HomeInteractionState(
            selectedProjectId = savedStateHandle.get<Long>(SELECTED_PROJECT_ID_KEY),
            isCreateProjectDialogVisible =
                savedStateHandle.get<Boolean>(CREATE_PROJECT_DIALOG_VISIBLE_KEY) ?: false,
            createProjectForm = CreateProjectFormState(
                name = savedStateHandle.get<String>(PROJECT_NAME_KEY).orEmpty(),
                description = savedStateHandle.get<String>(PROJECT_DESCRIPTION_KEY).orEmpty(),
            ),
            isCreateTaskDialogVisible =
                savedStateHandle.get<Boolean>(CREATE_TASK_DIALOG_VISIBLE_KEY) ?: false,
            createTaskForm = CreateTaskFormState(
                title = savedStateHandle.get<String>(TASK_TITLE_KEY).orEmpty(),
                description = savedStateHandle.get<String>(TASK_DESCRIPTION_KEY).orEmpty(),
                status = savedStateHandle.get<String>(TASK_STATUS_KEY)
                    ?.let { storedStatus ->
                        TaskStatus.entries.firstOrNull {
                            it.storageValue == storedStatus
                        }
                    }
                    ?: TaskStatus.TODO,
            ),
        ),
    )

    private val selectedProjectTasks = interactionState
        .map { it.selectedProjectId }
        .distinctUntilChanged()
        .flatMapLatest { projectId ->
            if (projectId == null || projectId <= 0) {
                flowOf(SelectedProjectTasks())
            } else {
                getProjectTasksUseCase(projectId).map { tasks ->
                    SelectedProjectTasks(projectId = projectId, tasks = tasks)
                }
            }
        }

    val uiState: StateFlow<HomeUiState> = combine(
        observeProjectsUseCase(),
        selectedProjectTasks,
        interactionState,
    ) { projects, projectTasks, interaction ->
        val selectedProjectId = interaction.selectedProjectId?.takeIf { selectedId ->
            projects.any { it.id == selectedId }
        }
        HomeUiState(
            projects = projects,
            tasks = projectTasks.tasks.takeIf {
                projectTasks.projectId == selectedProjectId
            }.orEmpty(),
            selectedProjectId = selectedProjectId,
            isCreateProjectDialogVisible = interaction.isCreateProjectDialogVisible,
            createProjectForm = interaction.createProjectForm,
            isCreateTaskDialogVisible =
                interaction.isCreateTaskDialogVisible && selectedProjectId != null,
            createTaskForm = interaction.createTaskForm,
            movingTaskIds = interaction.movingTaskIds,
            taskMoveError = interaction.taskMoveError,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OpenCreateProjectDialog -> openCreateProjectDialog()
            HomeAction.DismissCreateProjectDialog -> dismissCreateProjectDialog()
            is HomeAction.ProjectNameChanged -> updateProjectName(action.value)
            is HomeAction.ProjectDescriptionChanged -> updateProjectDescription(action.value)
            is HomeAction.ProjectSelected -> selectProject(action.id)
            HomeAction.CreateProject -> createProject()
            HomeAction.OpenCreateTaskDialog -> openCreateTaskDialog()
            HomeAction.DismissCreateTaskDialog -> dismissCreateTaskDialog()
            is HomeAction.TaskTitleChanged -> updateTaskTitle(action.value)
            is HomeAction.TaskDescriptionChanged -> updateTaskDescription(action.value)
            is HomeAction.TaskStatusChanged -> updateTaskStatus(action.value)
            HomeAction.CreateTask -> createTask()
            is HomeAction.TaskMoved -> moveTask(action.taskId, action.destination)
            HomeAction.DismissTaskMoveError -> dismissTaskMoveError()
        }
    }

    private fun openCreateProjectDialog() {
        updateInteraction { current ->
            current.copy(
                isCreateProjectDialogVisible = true,
                createProjectForm = CreateProjectFormState(),
            )
        }
    }

    private fun dismissCreateProjectDialog() {
        if (interactionState.value.createProjectForm.isSaving) return

        updateInteraction { current ->
            current.copy(
                isCreateProjectDialogVisible = false,
                createProjectForm = CreateProjectFormState(),
            )
        }
    }

    private fun updateProjectName(value: String) {
        val error = if (value.length > ProjectInputConstraints.NAME_MAX_LENGTH) {
            ProjectNameError.TOO_LONG
        } else {
            null
        }

        updateInteraction { current ->
            current.copy(
                createProjectForm = current.createProjectForm.copy(
                    name = value,
                    nameError = error,
                    submitError = null,
                ),
            )
        }
    }

    private fun updateProjectDescription(value: String) {
        val error = if (value.length > ProjectInputConstraints.DESCRIPTION_MAX_LENGTH) {
            ProjectDescriptionError.TOO_LONG
        } else {
            null
        }

        updateInteraction { current ->
            current.copy(
                createProjectForm = current.createProjectForm.copy(
                    description = value,
                    descriptionError = error,
                    submitError = null,
                ),
            )
        }
    }

    private fun selectProject(projectId: Long) {
        if (projectId <= 0) return
        updateInteraction { current ->
            current.copy(
                selectedProjectId = projectId,
                isCreateTaskDialogVisible = false,
                createTaskForm = CreateTaskFormState(),
            )
        }
    }

    private fun createProject() {
        val form = interactionState.value.createProjectForm
        if (form.isSaving) return

        val nameError = when {
            form.name.isBlank() -> ProjectNameError.REQUIRED
            form.name.length > ProjectInputConstraints.NAME_MAX_LENGTH -> ProjectNameError.TOO_LONG
            else -> null
        }
        val descriptionError = if (
            form.description.length > ProjectInputConstraints.DESCRIPTION_MAX_LENGTH
        ) {
            ProjectDescriptionError.TOO_LONG
        } else {
            null
        }

        if (nameError != null || descriptionError != null) {
            updateInteraction { current ->
                current.copy(
                    createProjectForm = current.createProjectForm.copy(
                        nameError = nameError,
                        descriptionError = descriptionError,
                    ),
                )
            }
            return
        }

        updateInteraction { current ->
            current.copy(
                createProjectForm = current.createProjectForm.copy(
                    isSaving = true,
                    submitError = null,
                ),
            )
        }

        viewModelScope.launch {
            try {
                val projectId = createProjectUseCase(
                    name = form.name,
                    description = form.description,
                )
                updateInteraction { current ->
                    current.copy(
                        selectedProjectId = projectId,
                        isCreateProjectDialogVisible = false,
                        createProjectForm = CreateProjectFormState(),
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                updateInteraction { current ->
                    current.copy(
                        createProjectForm = current.createProjectForm.copy(
                            isSaving = false,
                            submitError = ProjectSubmitError.UNKNOWN,
                        ),
                    )
                }
            }
        }
    }

    private fun openCreateTaskDialog() {
        if (interactionState.value.selectedProjectId == null) return

        updateInteraction { current ->
            current.copy(
                isCreateTaskDialogVisible = true,
                createTaskForm = CreateTaskFormState(),
            )
        }
    }

    private fun dismissCreateTaskDialog() {
        if (interactionState.value.createTaskForm.isSaving) return

        updateInteraction { current ->
            current.copy(
                isCreateTaskDialogVisible = false,
                createTaskForm = CreateTaskFormState(),
            )
        }
    }

    private fun updateTaskTitle(value: String) {
        val error = if (value.length > TaskInputConstraints.TITLE_MAX_LENGTH) {
            TaskTitleError.TOO_LONG
        } else {
            null
        }

        updateInteraction { current ->
            current.copy(
                createTaskForm = current.createTaskForm.copy(
                    title = value,
                    titleError = error,
                    submitError = null,
                ),
            )
        }
    }

    private fun updateTaskDescription(value: String) {
        val error = if (value.length > TaskInputConstraints.DESCRIPTION_MAX_LENGTH) {
            TaskDescriptionError.TOO_LONG
        } else {
            null
        }

        updateInteraction { current ->
            current.copy(
                createTaskForm = current.createTaskForm.copy(
                    description = value,
                    descriptionError = error,
                    submitError = null,
                ),
            )
        }
    }

    private fun updateTaskStatus(status: TaskStatus) {
        updateInteraction { current ->
            current.copy(
                createTaskForm = current.createTaskForm.copy(
                    status = status,
                    submitError = null,
                ),
            )
        }
    }

    private fun createTask() {
        val state = interactionState.value
        val projectId = state.selectedProjectId ?: return
        val form = state.createTaskForm
        if (form.isSaving) return

        val titleError = when {
            form.title.isBlank() -> TaskTitleError.REQUIRED
            form.title.length > TaskInputConstraints.TITLE_MAX_LENGTH -> TaskTitleError.TOO_LONG
            else -> null
        }
        val descriptionError = if (
            form.description.length > TaskInputConstraints.DESCRIPTION_MAX_LENGTH
        ) {
            TaskDescriptionError.TOO_LONG
        } else {
            null
        }

        if (titleError != null || descriptionError != null) {
            updateInteraction { current ->
                current.copy(
                    createTaskForm = current.createTaskForm.copy(
                        titleError = titleError,
                        descriptionError = descriptionError,
                    ),
                )
            }
            return
        }

        updateInteraction { current ->
            current.copy(
                createTaskForm = current.createTaskForm.copy(
                    isSaving = true,
                    submitError = null,
                ),
            )
        }

        viewModelScope.launch {
            try {
                createTaskUseCase(
                    projectId = projectId,
                    title = form.title,
                    description = form.description,
                    status = form.status,
                )
                updateInteraction { current ->
                    current.copy(
                        isCreateTaskDialogVisible = false,
                        createTaskForm = CreateTaskFormState(),
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                updateInteraction { current ->
                    current.copy(
                        createTaskForm = current.createTaskForm.copy(
                            isSaving = false,
                            submitError = TaskSubmitError.UNKNOWN,
                        ),
                    )
                }
            }
        }
    }

    private fun moveTask(taskId: Long, destination: TaskStatus) {
        val task = uiState.value.tasks.firstOrNull { it.id == taskId } ?: return
        val interaction = interactionState.value
        if (task.status == destination || taskId in interaction.movingTaskIds) return

        updateInteraction { current ->
            current.copy(
                movingTaskIds = current.movingTaskIds + taskId,
                taskMoveError = null,
            )
        }

        viewModelScope.launch {
            try {
                updateTaskStatusUseCase(task, destination)
                updateInteraction { current ->
                    current.copy(movingTaskIds = current.movingTaskIds - taskId)
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                updateInteraction { current ->
                    current.copy(
                        movingTaskIds = current.movingTaskIds - taskId,
                        taskMoveError = TaskMoveError.UNKNOWN,
                    )
                }
            }
        }
    }

    private fun dismissTaskMoveError() {
        updateInteraction { current ->
            current.copy(taskMoveError = null)
        }
    }

    private fun updateInteraction(
        transform: (HomeInteractionState) -> HomeInteractionState,
    ) {
        interactionState.update(transform)
        persistInteractionState(interactionState.value)
    }

    private fun persistInteractionState(state: HomeInteractionState) {
        savedStateHandle[SELECTED_PROJECT_ID_KEY] = state.selectedProjectId
        savedStateHandle[CREATE_PROJECT_DIALOG_VISIBLE_KEY] =
            state.isCreateProjectDialogVisible
        savedStateHandle[PROJECT_NAME_KEY] = state.createProjectForm.name
        savedStateHandle[PROJECT_DESCRIPTION_KEY] = state.createProjectForm.description
        savedStateHandle[CREATE_TASK_DIALOG_VISIBLE_KEY] =
            state.isCreateTaskDialogVisible
        savedStateHandle[TASK_TITLE_KEY] = state.createTaskForm.title
        savedStateHandle[TASK_DESCRIPTION_KEY] = state.createTaskForm.description
        savedStateHandle[TASK_STATUS_KEY] = state.createTaskForm.status.storageValue
    }

    private data class HomeInteractionState(
        val selectedProjectId: Long? = null,
        val isCreateProjectDialogVisible: Boolean = false,
        val createProjectForm: CreateProjectFormState = CreateProjectFormState(),
        val isCreateTaskDialogVisible: Boolean = false,
        val createTaskForm: CreateTaskFormState = CreateTaskFormState(),
        val movingTaskIds: Set<Long> = emptySet(),
        val taskMoveError: TaskMoveError? = null,
    )

    private data class SelectedProjectTasks(
        val projectId: Long? = null,
        val tasks: List<Task> = emptyList(),
    )

    private companion object {
        const val SELECTED_PROJECT_ID_KEY = "selectedProjectId"
        const val CREATE_PROJECT_DIALOG_VISIBLE_KEY = "isCreateProjectDialogVisible"
        const val PROJECT_NAME_KEY = "projectName"
        const val PROJECT_DESCRIPTION_KEY = "projectDescription"
        const val CREATE_TASK_DIALOG_VISIBLE_KEY = "isCreateTaskDialogVisible"
        const val TASK_TITLE_KEY = "taskTitle"
        const val TASK_DESCRIPTION_KEY = "taskDescription"
        const val TASK_STATUS_KEY = "taskStatus"
    }
}
