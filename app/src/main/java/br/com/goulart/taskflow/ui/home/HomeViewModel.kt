package br.com.goulart.taskflow.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.goulart.taskflow.domain.model.ProjectInputConstraints
import br.com.goulart.taskflow.domain.usecase.CreateProjectUseCase
import br.com.goulart.taskflow.domain.usecase.ObserveProjectsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    observeProjectsUseCase: ObserveProjectsUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
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
        ),
    )

    val uiState: StateFlow<HomeUiState> = combine(
        observeProjectsUseCase(),
        interactionState,
    ) { projects, interaction ->
        HomeUiState(
            projects = projects,
            selectedProjectId = interaction.selectedProjectId?.takeIf { selectedId ->
                projects.any { it.id == selectedId }
            },
            isCreateProjectDialogVisible = interaction.isCreateProjectDialogVisible,
            createProjectForm = interaction.createProjectForm,
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
        updateInteraction { current -> current.copy(selectedProjectId = projectId) }
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
    }

    private data class HomeInteractionState(
        val selectedProjectId: Long? = null,
        val isCreateProjectDialogVisible: Boolean = false,
        val createProjectForm: CreateProjectFormState = CreateProjectFormState(),
    )

    private companion object {
        const val SELECTED_PROJECT_ID_KEY = "selectedProjectId"
        const val CREATE_PROJECT_DIALOG_VISIBLE_KEY = "isCreateProjectDialogVisible"
        const val PROJECT_NAME_KEY = "projectName"
        const val PROJECT_DESCRIPTION_KEY = "projectDescription"
    }
}
