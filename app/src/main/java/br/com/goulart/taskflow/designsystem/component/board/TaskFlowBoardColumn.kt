package br.com.goulart.taskflow.designsystem.component.board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> TaskFlowBoardColumn(
    title: String,
    items: List<T>,
    modifier: Modifier = Modifier,
    onMoreClick: (() -> Unit)? = null,
    tone: TaskFlowStatusTone = TaskFlowStatusTone.Neutral,
    itemContent: @Composable (T) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.large,
    ) {
        Column {
            TaskFlowBoardColumnHeader(
                title = title,
                itemCount = items.size,
                onMoreClick = onMoreClick,
                tone = tone,
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 12.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = items,
                ) { item ->
                    itemContent(item)
                }
            }
        }
    }
}
