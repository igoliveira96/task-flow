package br.com.goulart.taskflow.ui.projects

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.data.model.Project
import br.com.goulart.taskflow.designsystem.component.header.TaskFlowPageHeader
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme
import br.com.goulart.taskflow.ui.projects.component.EditProjectDialog

private val ProjectCardMinWidth = 320.dp

@Composable
fun ProjectsScreen(
    uiState: ProjectsUiState,
    onAction: (ProjectsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.safeDrawingPadding()) {
            TaskFlowPageHeader(
                title = stringResource(R.string.projects_title),
                description = stringResource(R.string.projects_subtitle),
                overline = stringResource(R.string.projects_workspace),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            )

            if (uiState.projects.isEmpty()) {
                ProjectsEmptyState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(ProjectCardMinWidth),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = uiState.projects,
                        key = { it.id },
                    ) { project ->
                        ProjectCard(
                            project = project,
                            onEditClick = {
                                onAction(ProjectsAction.OpenEditProject(project.id))
                            },
                        )
                    }
                }
            }
        }
    }

    if (uiState.editingProject != null) {
        EditProjectDialog(
            state = uiState.editForm,
            onNameChange = { value ->
                onAction(ProjectsAction.ProjectNameChanged(value))
            },
            onDescriptionChange = { value ->
                onAction(ProjectsAction.ProjectDescriptionChanged(value))
            },
            onDismissRequest = {
                onAction(ProjectsAction.DismissEditProject)
            },
            onSaveClick = {
                onAction(ProjectsAction.SaveProject)
            },
        )
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = project.description.ifBlank {
                            stringResource(R.string.project_no_description)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        minLines = 2,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(R.string.edit),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun ProjectsEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
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
                text = stringResource(R.string.projects_empty_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = stringResource(R.string.projects_empty_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Projects - Phone", widthDp = 360, heightDp = 800, showBackground = true)
@Preview(name = "Projects - Tablet", widthDp = 1100, heightDp = 800, showBackground = true)
@Preview(
    name = "Projects - Dark",
    widthDp = 360,
    heightDp = 800,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ProjectsScreenPreview() {
    TaskFlowTheme {
        ProjectsScreen(
            uiState = ProjectsUiState(
                projects = listOf(
                    Project(
                        id = 1,
                        name = "Mobile app",
                        description = "Plan and deliver the next Android release.",
                        createdAt = 0,
                    ),
                    Project(
                        id = 2,
                        name = "Website",
                        description = "",
                        createdAt = 0,
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
