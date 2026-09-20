package br.com.goulart.taskflow.designsystem.component.board

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> TaskFlowBoardColumn(
    title: String,
    items: List<T>,
    modifier: Modifier = Modifier,
    onMoreClick: (() -> Unit)? = null,
    tone: TaskFlowStatusTone = TaskFlowStatusTone.Neutral,
    itemKey: ((T) -> Any)? = null,
    isDropTarget: Boolean = false,
    itemContent: @Composable (T) -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = if (isDropTarget) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        label = "board column container",
    )
    val tonalElevation by animateDpAsState(
        targetValue = if (isDropTarget) 4.dp else 0.dp,
        label = "board column elevation",
    )

    Surface(
        modifier = modifier,
        color = containerColor,
        shape = MaterialTheme.shapes.large,
        border = if (isDropTarget) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
        tonalElevation = tonalElevation,
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
                    key = itemKey,
                ) { item ->
                    itemContent(item)
                }
            }
        }
    }
}
