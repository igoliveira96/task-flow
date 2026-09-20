package br.com.goulart.taskflow.designsystem.component.board

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme

@Composable
fun TaskFlowTaskCard(
    taskId: String,
    title: String,
    description: String,
    assignee: String?,
    onClick: () -> Unit,
    selected: Boolean = false,
    showDragHandle: Boolean = false,
    dragHandleContentDescription: String? = null,
    dragHandleModifier: Modifier = Modifier,
    dragging: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val cardAlpha by animateFloatAsState(
        targetValue = if (dragging) 0.45f else 1f,
        label = "task card alpha",
    )
    val cardScale by animateFloatAsState(
        targetValue = if (dragging) 0.98f else 1f,
        label = "task card scale",
    )

    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = cardAlpha
                scaleX = cardScale
                scaleY = cardScale
            },
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                if (showDragHandle) {
                    Box(
                        modifier = dragHandleModifier.size(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DragIndicator,
                            contentDescription = dragHandleContentDescription,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckBox,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = taskId,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (!assignee.isNullOrBlank()) {
                    TaskFlowAssigneeAvatar(name = assignee)
                }
            }
        }
    }
}

@Preview(name = "Task card - Light", showBackground = true, widthDp = 296)
@Preview(
    name = "Task card - Dark",
    showBackground = true,
    widthDp = 296,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TaskFlowTaskCardPreview() {
    TaskFlowTheme {
        TaskFlowTaskCard(
            taskId = "TF-101",
            title = "Create project overview",
            description = "Show project progress and upcoming milestones.",
            assignee = "Ana Silva",
            onClick = {},
        )
    }
}
