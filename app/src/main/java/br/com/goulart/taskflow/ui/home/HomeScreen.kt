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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowBoardColumn
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowTaskCard
import br.com.goulart.taskflow.designsystem.component.header.TaskFlowPageHeader
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme

private val BoardContentPadding = 12.dp
private val BoardColumnSpacing = 12.dp
private val BoardColumnMinPreferredWidth = 200.dp
private val BoardColumnMaxPreferredWidth = 320.dp
private val BoardToolbarBreakpoint = 600.dp
private val ProjectSelectorPreferredWidth = 280.dp

data class ProjectSelectorOption(
    val id: Long,
    val name: String,
)

@Composable
fun HomeScreen(
    onTaskClick: (String) -> Unit,
    onCreateTaskClick: () -> Unit = {},
    projects: List<ProjectSelectorOption> = emptyList(),
    selectedProjectId: Long? = null,
    onProjectSelected: (Long) -> Unit = {},
    selectedTaskId: String? = null,
    modifier: Modifier = Modifier,
    singleColumn: Boolean = false,
    isPane: Boolean = false,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        shape = if (isPane) MaterialTheme.shapes.large else RectangleShape,
    ) {
        Column(
            modifier = Modifier.safeDrawingPadding(),
        ) {
            HomeBoardHeader(
                projects = projects,
                selectedProjectId = selectedProjectId,
                onProjectSelected = onProjectSelected,
                onCreateTaskClick = onCreateTaskClick,
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                val contentWidth = (maxWidth - BoardContentPadding * 2).coerceAtLeast(1.dp)
                val visibleColumnCount = if (singleColumn) {
                    1
                } else {
                    ((contentWidth + BoardColumnSpacing) /
                        (BoardColumnMinPreferredWidth + BoardColumnSpacing))
                        .toInt()
                        .coerceIn(1, homeMockColumns.size)
                }
                val availableColumnWidth =
                    (contentWidth - BoardColumnSpacing * (visibleColumnCount - 1)) / visibleColumnCount
                val columnWidth = if (singleColumn) {
                    availableColumnWidth
                } else {
                    availableColumnWidth.coerceAtMost(BoardColumnMaxPreferredWidth)
                }
                val listState = rememberLazyListState()

                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    flingBehavior = if (singleColumn) {
                        rememberSnapFlingBehavior(listState)
                    } else {
                        ScrollableDefaults.flingBehavior()
                    },
                    contentPadding = PaddingValues(
                        start = BoardContentPadding,
                        end = BoardContentPadding,
                        bottom = BoardContentPadding,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(BoardColumnSpacing),
                ) {
                    items(homeMockColumns, key = { it.id }) { column ->
                        TaskFlowBoardColumn(
                            title = column.title,
                            items = column.tasks,
                            tone = column.tone,
                            modifier = Modifier
                                .width(columnWidth)
                                .fillMaxHeight(),
                        ) { task ->
                            TaskFlowTaskCard(
                                taskId = task.id,
                                title = task.title,
                                description = task.description,
                                assignee = task.assignee,
                                onClick = { onTaskClick(task.id) },
                                selected = task.id == selectedTaskId,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeBoardHeader(
    projects: List<ProjectSelectorOption>,
    selectedProjectId: Long?,
    onProjectSelected: (Long) -> Unit,
    onCreateTaskClick: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BoardContentPadding, vertical = 24.dp),
    ) {
        val compact = maxWidth < BoardToolbarBreakpoint
        val header: @Composable (Modifier) -> Unit = { modifier ->
            TaskFlowPageHeader(
                title = stringResource(R.string.home_title),
                description = stringResource(
                    R.string.home_board_subtitle,
                    homeMockColumns.sumOf { it.tasks.size },
                    homeMockColumns.size,
                ),
                overline = stringResource(R.string.home_workspace),
                modifier = modifier,
            )
        }

        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                header(Modifier.fillMaxWidth())
                HomeBoardActions(
                    projects = projects,
                    selectedProjectId = selectedProjectId,
                    onProjectSelected = onProjectSelected,
                    onCreateTaskClick = onCreateTaskClick,
                    compact = true,
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                header(Modifier.weight(1f))
                HomeBoardActions(
                    projects = projects,
                    selectedProjectId = selectedProjectId,
                    onProjectSelected = onProjectSelected,
                    onCreateTaskClick = onCreateTaskClick,
                    compact = false,
                )
            }
        }
    }
}

@Composable
private fun HomeBoardActions(
    projects: List<ProjectSelectorOption>,
    selectedProjectId: Long?,
    onProjectSelected: (Long) -> Unit,
    onCreateTaskClick: () -> Unit,
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
            onClick = onCreateTaskClick,
            modifier = modifier,
            contentPadding = if (compact) {
                PaddingValues(horizontal = 12.dp)
            } else {
                ButtonDefaults.ContentPadding
            },
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
            Text(
                text = stringResource(R.string.new_task),
                modifier = Modifier.padding(start = 8.dp),
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
private fun ProjectSelector(
    projects: List<ProjectSelectorOption>,
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

@Preview(name = "Home - Phone", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(name = "Home - Tablet", showBackground = true, widthDp = 1100, heightDp = 800)
@Preview(
    name = "Home - Dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun HomeScreenPreview() {
    TaskFlowTheme {
        HomeScreen(onTaskClick = {})
    }
}
