package br.com.goulart.taskflow.ui.projects.component

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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.domain.model.ProjectInputConstraints
import br.com.goulart.taskflow.ui.projects.EditProjectDescriptionError
import br.com.goulart.taskflow.ui.projects.EditProjectFormState
import br.com.goulart.taskflow.ui.projects.EditProjectNameError
import br.com.goulart.taskflow.ui.projects.EditProjectSubmitError

private val DialogMaxWidth = 560.dp
private val DialogMaxHeight = 640.dp

@Composable
fun EditProjectDialog(
    state: EditProjectFormState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val nameError = when (state.nameError) {
        EditProjectNameError.REQUIRED -> stringResource(R.string.project_name_required)
        EditProjectNameError.TOO_LONG -> stringResource(
            R.string.project_name_too_long,
            ProjectInputConstraints.NAME_MAX_LENGTH,
        )
        null -> null
    }
    val descriptionError = when (state.descriptionError) {
        EditProjectDescriptionError.TOO_LONG -> stringResource(
            R.string.project_description_too_long,
            ProjectInputConstraints.DESCRIPTION_MAX_LENGTH,
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
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.edit_project),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                text = stringResource(R.string.edit_project_description),
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
                                contentDescription = stringResource(R.string.close_edit_project),
                            )
                        }
                    }

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        enabled = !state.isSaving,
                        singleLine = true,
                        label = { Text(stringResource(R.string.project_name)) },
                        supportingText = {
                            Text(
                                text = nameError ?: stringResource(
                                    R.string.character_count,
                                    state.name.length,
                                    ProjectInputConstraints.NAME_MAX_LENGTH,
                                ),
                            )
                        },
                        isError = nameError != null,
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
                        label = { Text(stringResource(R.string.project_description_optional)) },
                        supportingText = {
                            Text(
                                text = descriptionError ?: stringResource(
                                    R.string.character_count,
                                    state.description.length,
                                    ProjectInputConstraints.DESCRIPTION_MAX_LENGTH,
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
                            onDone = {
                                focusManager.clearFocus()
                                if (state.canSubmit) onSaveClick()
                            },
                        ),
                    )

                    if (state.submitError == EditProjectSubmitError.UNKNOWN) {
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
                                Text(stringResource(R.string.edit_project_error))
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
                            onClick = onSaveClick,
                            enabled = state.canSubmit,
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text(stringResource(R.string.save))
                            }
                        }
                    }
                }
            }
        }
    }
}
