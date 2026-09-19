package br.com.goulart.taskflow.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
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

@Composable
fun HomeScreen(
    onTaskClick: (String) -> Unit,
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
            TaskFlowPageHeader(
                title = stringResource(R.string.home_title),
                description = stringResource(
                    R.string.home_board_subtitle,
                    homeMockColumns.sumOf { it.tasks.size },
                    homeMockColumns.size,
                ),
                overline = stringResource(R.string.home_workspace),
                modifier = Modifier.padding(horizontal = BoardContentPadding, vertical = 24.dp),
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
