package br.com.goulart.taskflow.ui.projects

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.goulart.taskflow.domain.model.ProjectInputConstraints
import br.com.goulart.taskflow.domain.usecase.ObserveProjectsUseCase
import br.com.goulart.taskflow.domain.usecase.UpdateProjectUseCase
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
class ProjectsViewModel(
    observeProjectsUseCase: ObserveProjectsUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val interactionState = MutableStateFlow(
        ProjectsInteractionState(
            editingProjectId = savedStateHandle.get<Long>(EDITING_PROJECT_ID_KEY),
            editForm = EditProjectFormState(
                name = savedStateHandle.get<String>(PROJECT_NAME_KEY).orEmpty(),
                description = savedStateHandle.get<String>(PROJECT_DESCRIPTION_KEY).orEmpty(),
            ),
        ),
    )

    val uiState: StateFlow<ProjectsUiState> = combine(
        observeProjectsUseCase(),
        interactionState,
    ) { projects, interaction ->
        val editingProjectId = interaction.editingProjectId?.takeIf { editingId ->
            projects.any { it.id == editingId }
        }
        ProjectsUiState(
            projects = projects,
            editingProjectId = editingProjectId,
            editForm = interaction.editForm,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProjectsUiState(),
    )

    fun onAction(action: ProjectsAction) {
        when (action) {
            is ProjectsAction.OpenEditProject -> openEditProject(action.projectId)
            ProjectsAction.DismissEditProject -> dismissEditProject()
            is ProjectsAction.ProjectNameChanged -> updateProjectName(action.value)
            is ProjectsAction.ProjectDescriptionChanged ->
                updateProjectDescription(action.value)
            ProjectsAction.SaveProject -> saveProject()
        }
    }

    private fun openEditProject(projectId: Long) {
        val project = uiState.value.projects.firstOrNull { it.id == projectId } ?: return
        updateInteraction { current ->
            current.copy(
                editingProjectId = project.id,
                editForm = EditProjectFormState(
                    name = project.name,
                    description = project.description,
                ),
            )
        }
    }

    private fun dismissEditProject() {
        if (interactionState.value.editForm.isSaving) return
        updateInteraction { ProjectsInteractionState() }
    }

    private fun updateProjectName(value: String) {
        val error = if (value.length > ProjectInputConstraints.NAME_MAX_LENGTH) {
            EditProjectNameError.TOO_LONG
        } else {
            null
        }
        updateInteraction { current ->
            current.copy(
                editForm = current.editForm.copy(
                    name = value,
                    nameError = error,
                    submitError = null,
                ),
            )
        }
    }

    private fun updateProjectDescription(value: String) {
        val error = if (value.length > ProjectInputConstraints.DESCRIPTION_MAX_LENGTH) {
            EditProjectDescriptionError.TOO_LONG
        } else {
            null
        }
        updateInteraction { current ->
            current.copy(
                editForm = current.editForm.copy(
                    description = value,
                    descriptionError = error,
                    submitError = null,
                ),
            )
        }
    }

    private fun saveProject() {
        val project = uiState.value.editingProject ?: return
        val form = interactionState.value.editForm
        if (form.isSaving) return

        val nameError = when {
            form.name.isBlank() -> EditProjectNameError.REQUIRED
            form.name.length > ProjectInputConstraints.NAME_MAX_LENGTH ->
                EditProjectNameError.TOO_LONG
            else -> null
        }
        val descriptionError = if (
            form.description.length > ProjectInputConstraints.DESCRIPTION_MAX_LENGTH
        ) {
            EditProjectDescriptionError.TOO_LONG
        } else {
            null
        }

        if (nameError != null || descriptionError != null) {
            updateInteraction { current ->
                current.copy(
                    editForm = current.editForm.copy(
                        nameError = nameError,
                        descriptionError = descriptionError,
                    ),
                )
            }
            return
        }

        updateInteraction { current ->
            current.copy(
                editForm = current.editForm.copy(
                    isSaving = true,
                    submitError = null,
                ),
            )
        }

        viewModelScope.launch {
            try {
                updateProjectUseCase(
                    project = project,
                    name = form.name,
                    description = form.description,
                )
                updateInteraction { ProjectsInteractionState() }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                updateInteraction { current ->
                    current.copy(
                        editForm = current.editForm.copy(
                            isSaving = false,
                            submitError = EditProjectSubmitError.UNKNOWN,
                        ),
                    )
                }
            }
        }
    }

    private fun updateInteraction(
        transform: (ProjectsInteractionState) -> ProjectsInteractionState,
    ) {
        interactionState.update(transform)
        persistInteractionState(interactionState.value)
    }

    private fun persistInteractionState(state: ProjectsInteractionState) {
        savedStateHandle[EDITING_PROJECT_ID_KEY] = state.editingProjectId
        savedStateHandle[PROJECT_NAME_KEY] = state.editForm.name
        savedStateHandle[PROJECT_DESCRIPTION_KEY] = state.editForm.description
    }

    private data class ProjectsInteractionState(
        val editingProjectId: Long? = null,
        val editForm: EditProjectFormState = EditProjectFormState(),
    )

    private companion object {
        const val EDITING_PROJECT_ID_KEY = "editingProjectId"
        const val PROJECT_NAME_KEY = "editingProjectName"
        const val PROJECT_DESCRIPTION_KEY = "editingProjectDescription"
    }
}
