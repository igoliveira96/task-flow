package br.com.goulart.taskflow.ui.taskdetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowAssigneeAvatar
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowStatusBadge
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowStatusTone
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme

@Composable
fun TaskDetailsScreen(
    taskId: String,
    title: String,
    description: String,
    assignee: String?,
    status: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    isSupportingPane: Boolean = false,
    statusTone: TaskFlowStatusTone = TaskFlowStatusTone.Neutral,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = if (isSupportingPane) MaterialTheme.shapes.large else RectangleShape,
    ) {
        Column(modifier = Modifier.safeDrawingPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!isSupportingPane) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_board),
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = stringResource(R.string.task_details_title),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = taskId,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (isSupportingPane) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.close_task_details),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = title, style = MaterialTheme.typography.headlineSmall)
                    TaskFlowStatusBadge(label = status, tone = statusTone)
                }
                TaskDetailsField(
                    label = stringResource(R.string.task_description),
                    value = description.ifBlank {
                        stringResource(R.string.task_no_description)
                    },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.task_assignee),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!assignee.isNullOrBlank()) {
                            TaskFlowAssigneeAvatar(name = assignee)
                        }
                        Text(
                            text = assignee?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.task_unassigned),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskDetailsField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(name = "Task details - Phone", widthDp = 360, heightDp = 800)
@Preview(
    name = "Task details - Dark",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TaskDetailsScreenPreview() {
    TaskFlowTheme {
        TaskDetailsScreen(
            taskId = "TF-101",
            title = "Create project overview",
            description = "Show project progress and upcoming milestones.",
            assignee = "Ana Silva",
            status = "To do",
            onClose = {},
        )
    }
}
