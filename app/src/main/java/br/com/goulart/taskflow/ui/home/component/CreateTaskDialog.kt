package br.com.goulart.taskflow.ui.home.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.data.model.TaskStatus
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme
import br.com.goulart.taskflow.domain.model.TaskInputConstraints
import br.com.goulart.taskflow.ui.home.CreateTaskFormState
import br.com.goulart.taskflow.ui.home.TaskDescriptionError
import br.com.goulart.taskflow.ui.home.TaskSubmitError
import br.com.goulart.taskflow.ui.home.TaskTitleError

private val DialogMaxWidth = 560.dp
private val DialogMaxHeight = 720.dp

@Composable
fun CreateTaskDialog(
    projectName: String,
    state: CreateTaskFormState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    onDismissRequest: () -> Unit,
    onCreateClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val titleError = when (state.titleError) {
        TaskTitleError.REQUIRED -> stringResource(R.string.task_title_required)
        TaskTitleError.TOO_LONG -> stringResource(
            R.string.task_title_too_long,
            TaskInputConstraints.TITLE_MAX_LENGTH,
        )
        null -> null
    }
    val descriptionError = when (state.descriptionError) {
        TaskDescriptionError.TOO_LONG -> stringResource(
            R.string.task_description_too_long,
            TaskInputConstraints.DESCRIPTION_MAX_LENGTH,
        )
        null -> null
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = !state.isSaving,
            dismissOnClickOutside = !state.isSaving,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = DialogMaxWidth)
                    .fillMaxWidth()
                    .heightIn(max = DialogMaxHeight),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddTask,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.create_task),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(
                                    R.string.create_task_description,
                                    projectName,
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(
                            onClick = onDismissRequest,
                            enabled = !state.isSaving,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.close_create_task),
                            )
                        }
                    }

                    OutlinedTextField(
                        value = state.title,
                        onValueChange = onTitleChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        enabled = !state.isSaving,
                        singleLine = true,
                        label = { Text(stringResource(R.string.task_title)) },
                        placeholder = { Text(stringResource(R.string.task_title_placeholder)) },
                        supportingText = {
                            Text(
                                text = titleError ?: stringResource(
                                    R.string.character_count,
                                    state.title.length,
                                    TaskInputConstraints.TITLE_MAX_LENGTH,
                                ),
                            )
                        },
                        isError = titleError != null,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                    )

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving,
                        minLines = 3,
                        maxLines = 5,
                        label = { Text(stringResource(R.string.task_description_optional)) },
                        placeholder = {
                            Text(stringResource(R.string.task_description_placeholder))
                        },
                        supportingText = {
                            Text(
                                text = descriptionError ?: stringResource(
                                    R.string.character_count,
                                    state.description.length,
                                    TaskInputConstraints.DESCRIPTION_MAX_LENGTH,
                                ),
                            )
                        },
                        isError = descriptionError != null,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() },
                        ),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.task_status),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            TaskStatus.entries.forEachIndexed { index, status ->
                                SegmentedButton(
                                    selected = state.status == status,
                                    onClick = { onStatusChange(status) },
                                    enabled = !state.isSaving,
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = TaskStatus.entries.size,
                                    ),
                                    label = {
                                        Text(
                                            text = stringResource(status.labelResource()),
                                            maxLines = 1,
                                        )
                                    },
                                )
                            }
                        }
                    }

                    if (state.submitError == TaskSubmitError.UNKNOWN) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.WarningAmber,
                                    contentDescription = null,
                                )
                                Text(
                                    text = stringResource(R.string.create_task_error),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 12.dp,
                            alignment = Alignment.End,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = onDismissRequest,
                            enabled = !state.isSaving,
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                        FilledTonalButton(
                            onClick = onCreateClick,
                            enabled = state.canSubmit,
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text(stringResource(R.string.create))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun TaskStatus.labelResource() = when (this) {
    TaskStatus.TODO -> R.string.task_status_todo
    TaskStatus.IN_PROGRESS -> R.string.task_status_in_progress
    TaskStatus.DONE -> R.string.task_status_done
}

@Preview(name = "Create task dialog - Light", showBackground = true)
@Preview(
    name = "Create task dialog - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CreateTaskDialogPreview() {
    TaskFlowTheme {
        CreateTaskDialog(
            projectName = "Mobile app",
            state = CreateTaskFormState(),
            onTitleChange = {},
            onDescriptionChange = {},
            onStatusChange = {},
            onDismissRequest = {},
            onCreateClick = {},
        )
    }
}
