package br.com.goulart.taskflow.ui.home

import android.content.res.Configuration
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowBoardColumn
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowTaskCard
import br.com.goulart.taskflow.designsystem.component.header.TaskFlowPageHeader
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme

@Composable
fun HomeScreen(
    onTaskClick: (String) -> Unit,
    selectedTaskId: String? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.safeDrawingPadding(),
        ) {
            TaskFlowPageHeader(
                title = stringResource(R.string.home_title),
                description = stringResource(R.string.home_board_subtitle),
                modifier = Modifier.padding(20.dp),
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                val columnWidth = (maxWidth - 48.dp).coerceIn(1.dp, 320.dp)

                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(homeMockColumns, key = { it.id }) { column ->
                        TaskFlowBoardColumn(
                            title = column.title,
                            items = column.tasks,
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
