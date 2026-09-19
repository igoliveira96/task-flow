package br.com.goulart.taskflow.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.data.model.Project
import br.com.goulart.taskflow.data.model.Task
import br.com.goulart.taskflow.data.model.TaskStatus
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowBoardColumn
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowStatusTone
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowTaskCard
import br.com.goulart.taskflow.designsystem.component.header.TaskFlowPageHeader
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme
import br.com.goulart.taskflow.ui.home.component.CreateProjectDialog
import br.com.goulart.taskflow.ui.home.component.CreateTaskDialog

private val HomeContentPadding = 12.dp
private val BoardColumnSpacing = 12.dp
private val BoardColumnMinPreferredWidth = 200.dp
private val BoardColumnMaxPreferredWidth = 320.dp
private val HomeToolbarBreakpoint = 600.dp
private val ProjectSelectorPreferredWidth = 280.dp
private val EmptyStateMaxWidth = 440.dp

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onTaskClick: (Long) -> Unit = {},
    selectedTaskId: Long? = null,
    modifier: Modifier = Modifier,
    isPane: Boolean = false,
) {
    val selectedProject = uiState.selectedProject

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        shape = if (isPane) MaterialTheme.shapes.large else RectangleShape,
    ) {
        Column(
            modifier = Modifier.safeDrawingPadding(),
        ) {
            HomeHeader(
                projects = uiState.projects,
                selectedProjectId = uiState.selectedProjectId,
                taskCount = uiState.tasks.size,
                onProjectSelected = { projectId ->
                    onAction(HomeAction.ProjectSelected(projectId))
                },
                onPrimaryActionClick = {
                    onAction(
                        if (selectedProject == null) {
                            HomeAction.OpenCreateProjectDialog
                        } else {
                            HomeAction.OpenCreateTaskDialog
                        },
                    )
                },
            )
            if (selectedProject == null || uiState.tasks.isEmpty()) {
                HomeEmptyState(
                    title = stringResource(
                        if (selectedProject == null) {
                            R.string.home_empty_title
                        } else {
                            R.string.project_empty_title
                        },
                    ),
                    description = stringResource(
                        if (selectedProject == null) {
                            R.string.home_empty_description
                        } else {
                            R.string.project_empty_description
                        },
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            } else {
                HomeBoard(
                    tasks = uiState.tasks,
                    selectedTaskId = selectedTaskId,
                    onTaskClick = onTaskClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }
        }
    }

    if (uiState.isCreateProjectDialogVisible) {
        CreateProjectDialog(
            state = uiState.createProjectForm,
            onNameChange = { value ->
                onAction(HomeAction.ProjectNameChanged(value))
            },
            onDescriptionChange = { value ->
                onAction(HomeAction.ProjectDescriptionChanged(value))
            },
            onDismissRequest = {
                onAction(HomeAction.DismissCreateProjectDialog)
            },
            onCreateClick = {
                onAction(HomeAction.CreateProject)
            },
        )
    }

    if (uiState.isCreateTaskDialogVisible && selectedProject != null) {
        CreateTaskDialog(
            projectName = selectedProject.name,
            state = uiState.createTaskForm,
            onTitleChange = { value ->
                onAction(HomeAction.TaskTitleChanged(value))
            },
            onDescriptionChange = { value ->
                onAction(HomeAction.TaskDescriptionChanged(value))
            },
            onStatusChange = { status ->
                onAction(HomeAction.TaskStatusChanged(status))
            },
            onDismissRequest = {
                onAction(HomeAction.DismissCreateTaskDialog)
            },
            onCreateClick = {
                onAction(HomeAction.CreateTask)
            },
        )
    }
}

@Composable
private fun HomeHeader(
    projects: List<Project>,
    selectedProjectId: Long?,
    taskCount: Int,
    onProjectSelected: (Long) -> Unit,
    onPrimaryActionClick: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HomeContentPadding, vertical = 24.dp),
    ) {
        val compact = maxWidth < HomeToolbarBreakpoint
        val selectedProject = projects.firstOrNull { it.id == selectedProjectId }
        val header: @Composable (Modifier) -> Unit = { modifier ->
            TaskFlowPageHeader(
                title = stringResource(R.string.home_title),
                description = if (selectedProject == null) {
                    stringResource(R.string.home_board_subtitle)
                } else {
                    pluralStringResource(
                        R.plurals.home_project_subtitle,
                        taskCount,
                        taskCount,
                        selectedProject.name,
                    )
                },
                overline = stringResource(R.string.home_workspace),
                modifier = modifier,
            )
        }

        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                header(Modifier.fillMaxWidth())
                HomeActions(
                    projects = projects,
                    selectedProjectId = selectedProjectId,
                    onProjectSelected = onProjectSelected,
                    onPrimaryActionClick = onPrimaryActionClick,
                    compact = true,
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                header(Modifier.weight(1f))
                HomeActions(
                    projects = projects,
                    selectedProjectId = selectedProjectId,
                    onProjectSelected = onProjectSelected,
                    onPrimaryActionClick = onPrimaryActionClick,
                    compact = false,
                )
            }
        }
    }
}

@Composable
private fun HomeActions(
    projects: List<Project>,
    selectedProjectId: Long?,
    onProjectSelected: (Long) -> Unit,
    onPrimaryActionClick: () -> Unit,
    compact: Boolean,
) {
    val selector: @Composable (Modifier) -> Unit = { modifier ->
        ProjectSelector(
            projects = projects,
            selectedProjectId = selectedProjectId,
            onProjectSelected = onProjectSelected,
            compact = compact,
            modifier = modifier,
        )
    }
    val createButton: @Composable (Modifier) -> Unit = { modifier ->
        FilledTonalButton(
            onClick = onPrimaryActionClick,
            modifier = modifier,
            contentPadding = if (compact) {
                PaddingValues(horizontal = 12.dp)
            } else {
                ButtonDefaults.ContentPadding
            },
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
            Text(
                text = stringResource(
                    if (selectedProjectId == null) {
                        R.string.new_project
                    } else {
                        R.string.new_task
                    },
                ),
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 1,
            )
        }
    }

    if (compact) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            selector(Modifier.weight(1.15f))
            createButton(Modifier.weight(1f))
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            selector(Modifier.width(ProjectSelectorPreferredWidth))
            createButton(Modifier)
        }
    }
}

@Composable
private fun HomeBoard(
    tasks: List<Task>,
    selectedTaskId: Long?,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val contentWidth = (maxWidth - HomeContentPadding * 2).coerceAtLeast(1.dp)
        val visibleColumnCount = (
            (contentWidth + BoardColumnSpacing) /
                (BoardColumnMinPreferredWidth + BoardColumnSpacing)
            ).toInt().coerceIn(1, TaskStatus.entries.size)
        val availableColumnWidth =
            (contentWidth - BoardColumnSpacing * (visibleColumnCount - 1)) /
                visibleColumnCount
        val columnWidth = availableColumnWidth.coerceAtMost(BoardColumnMaxPreferredWidth)
        val listState = rememberLazyListState()

        LazyRow(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            flingBehavior = if (visibleColumnCount == 1) {
                rememberSnapFlingBehavior(listState)
            } else {
                ScrollableDefaults.flingBehavior()
            },
            contentPadding = PaddingValues(
                start = HomeContentPadding,
                end = HomeContentPadding,
                bottom = HomeContentPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(BoardColumnSpacing),
        ) {
            items(TaskStatus.entries, key = { it.storageValue }) { status ->
                val statusTasks = tasks.filter { it.status == status }
                TaskFlowBoardColumn(
                    title = stringResource(status.titleResource()),
                    items = statusTasks,
                    tone = status.tone(),
                    modifier = Modifier
                        .width(columnWidth)
                        .fillMaxHeight(),
                ) { task ->
                    TaskFlowTaskCard(
                        taskId = task.code,
                        title = task.title,
                        description = task.description,
                        assignee = task.assignee?.name,
                        selected = task.id == selectedTaskId,
                        onClick = { onTaskClick(task.id) },
                    )
                }
            }
        }
    }
}

private fun TaskStatus.titleResource() = when (this) {
    TaskStatus.TODO -> R.string.task_status_todo
    TaskStatus.IN_PROGRESS -> R.string.task_status_in_progress
    TaskStatus.DONE -> R.string.task_status_done
}

private fun TaskStatus.tone() = when (this) {
    TaskStatus.TODO -> TaskFlowStatusTone.Neutral
    TaskStatus.IN_PROGRESS -> TaskFlowStatusTone.Information
    TaskStatus.DONE -> TaskFlowStatusTone.Success
}

@Composable
private fun HomeEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(HomeContentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = EmptyStateMaxWidth)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                Icon(
                    imageVector = Icons.Outlined.ViewKanban,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp),
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ProjectSelector(
    projects: List<Project>,
    selectedProjectId: Long?,
    onProjectSelected: (Long) -> Unit,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedProjectName = projects.firstOrNull { it.id == selectedProjectId }?.name.orEmpty()

    Box(modifier = modifier) {
        FilledTonalButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            ),
            contentPadding = if (compact) {
                PaddingValues(horizontal = 12.dp)
            } else {
                ButtonDefaults.ContentPadding
            },
        ) {
            Icon(
                imageVector = Icons.Outlined.FolderOpen,
                contentDescription = null,
            )
            Text(
                text = selectedProjectName.ifEmpty {
                    stringResource(
                        if (compact) R.string.project else R.string.select_project,
                    )
                },
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                imageVector = if (expanded) {
                    Icons.Outlined.KeyboardArrowUp
                } else {
                    Icons.Outlined.KeyboardArrowDown
                },
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (projects.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.no_projects_available)) },
                    onClick = {},
                    enabled = false,
                )
            } else {
                projects.forEach { project ->
                    DropdownMenuItem(
                        text = { Text(project.name) },
                        onClick = {
                            onProjectSelected(project.id)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview(name = "Home empty - Phone", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(name = "Home empty - Tablet", showBackground = true, widthDp = 1100, heightDp = 800)
@Preview(
    name = "Home empty - Dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun HomeScreenPreview() {
    TaskFlowTheme {
        HomeScreen(
            uiState = HomeUiState(),
            onAction = {},
        )
    }
}
