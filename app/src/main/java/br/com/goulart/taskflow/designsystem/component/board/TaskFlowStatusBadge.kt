package br.com.goulart.taskflow.designsystem.component.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class TaskFlowStatusTone { Neutral, Information, Success }

@Composable
internal fun TaskFlowStatusTone.contentColor(): Color = when (this) {
    TaskFlowStatusTone.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
    TaskFlowStatusTone.Information -> MaterialTheme.colorScheme.primary
    TaskFlowStatusTone.Success -> MaterialTheme.colorScheme.tertiary
}

@Composable
fun TaskFlowStatusBadge(
    label: String,
    tone: TaskFlowStatusTone,
    modifier: Modifier = Modifier,
) {
    val containerColor = when (tone) {
        TaskFlowStatusTone.Neutral -> MaterialTheme.colorScheme.surfaceContainerHigh
        TaskFlowStatusTone.Information -> MaterialTheme.colorScheme.primaryContainer
        TaskFlowStatusTone.Success -> MaterialTheme.colorScheme.tertiaryContainer
    }
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = tone.contentColor(),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(6.dp).background(tone.contentColor(), CircleShape))
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
