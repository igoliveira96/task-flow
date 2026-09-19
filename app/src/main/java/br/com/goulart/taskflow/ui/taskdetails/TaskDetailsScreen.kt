package br.com.goulart.taskflow.ui.taskdetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.designsystem.component.header.TaskFlowPageHeader
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme

@Composable
fun TaskDetailsScreen(
    taskId: String,
    title: String,
    description: String,
    assignee: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    isSupportingPane: Boolean = false,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Box {
            Column(modifier = Modifier.safeDrawingPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    Text(
                        text = stringResource(R.string.task_details_title),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (isSupportingPane) {
                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.close_task_details),
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    TaskFlowPageHeader(title = title, description = taskId)
                    TaskDetailsField(
                        label = stringResource(R.string.task_description),
                        value = description,
                    )
                    TaskDetailsField(
                        label = stringResource(R.string.task_assignee),
                        value = assignee,
                    )
                }
            }
            if (isSupportingPane) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterStart),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
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
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
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
            onClose = {},
        )
    }
}
