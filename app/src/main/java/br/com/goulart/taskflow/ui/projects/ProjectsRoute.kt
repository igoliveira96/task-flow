package br.com.goulart.taskflow.ui.projects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProjectsRoute(
    modifier: Modifier = Modifier,
    viewModel: ProjectsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProjectsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}
