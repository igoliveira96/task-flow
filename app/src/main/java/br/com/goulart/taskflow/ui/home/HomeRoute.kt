package br.com.goulart.taskflow.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.data.model.TaskStatus
import br.com.goulart.taskflow.designsystem.component.board.TaskFlowStatusTone
import br.com.goulart.taskflow.ui.home.navigation.HomeDestination
import br.com.goulart.taskflow.ui.home.navigation.HomeNavigationState
import br.com.goulart.taskflow.ui.home.navigation.rememberHomeNavigationState
import br.com.goulart.taskflow.ui.taskdetails.TaskDetailsScreen
import org.koin.compose.viewmodel.koinViewModel

private const val BoardToDetailsPreferredWidthRatio = 2
private val PaneSpacing = 8.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    navigationState: HomeNavigationState = rememberHomeNavigationState(),
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val adaptiveDirective = calculatePaneScaffoldDirective(windowAdaptiveInfo)
    val isTabletop = windowAdaptiveInfo.windowPosture.isTabletop
    val supportsSideBySide = !isTabletop && adaptiveDirective.maxHorizontalPartitions > 1
    val hasSelectedTask = navigationState.selectedTaskId != null
    val directive = adaptiveDirective.copy(
        maxHorizontalPartitions = if (!hasSelectedTask || isTabletop) {
            1
        } else {
            adaptiveDirective.maxHorizontalPartitions.coerceAtMost(2)
        },
        maxVerticalPartitions = if (hasSelectedTask && isTabletop) {
            adaptiveDirective.maxVerticalPartitions
        } else {
            1
        },
        horizontalPartitionSpacerSize = PaneSpacing,
        verticalPartitionSpacerSize = if (isTabletop) {
            adaptiveDirective.verticalPartitionSpacerSize
        } else {
            0.dp
        },
    )
    val supportingPaneStrategy = rememberSupportingPaneSceneStrategy<NavKey>(
        shouldHandleSinglePaneLayout = supportsSideBySide || isTabletop,
        directive = directive,
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
    )

    NavDisplay(
        backStack = navigationState.backStack,
        onBack = navigationState::closeTask,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .then(
                if (supportsSideBySide) {
                    Modifier.safeDrawingPadding().padding(PaneSpacing)
                } else {
                    Modifier
                },
            ),
        sceneStrategies = listOf(supportingPaneStrategy),
        transitionSpec = {
            slideInHorizontally(tween(300)) { it } togetherWith
                slideOutHorizontally(tween(300)) { -it / 4 }
        },
        popTransitionSpec = {
            slideInHorizontally(tween(300)) { -it / 4 } togetherWith
                slideOutHorizontally(tween(300)) { it }
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(tween(300)) { -it / 4 } togetherWith
                slideOutHorizontally(tween(300)) { it }
        },
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        entryProvider = entryProvider {
            entry<HomeDestination.Board>(
                metadata = SupportingPaneSceneStrategy.mainPane() +
                    SupportingPaneSceneStrategy.preferredPaneSize(
                        width = directive.defaultPanePreferredWidth *
                            BoardToDetailsPreferredWidthRatio,
                    ) +
                    SupportingPaneSceneStrategy.paneAnimation(
                        boundsAnimationSpec = tween(300),
                    ),
            ) {
                HomeScreen(
                    uiState = uiState,
                    onAction = { action ->
                        if (action is HomeAction.ProjectSelected) {
                            navigationState.closeTask()
                        }
                        viewModel.onAction(action)
                    },
                    onTaskClick = navigationState::openTask,
                    selectedTaskId = navigationState.selectedTaskId,
                    modifier = if (isTabletop && hasSelectedTask) {
                        Modifier.consumeWindowInsets(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
                        )
                    } else {
                        Modifier
                    },
                    isPane = supportsSideBySide,
                )
            }
            entry<HomeDestination.TaskDetails>(
                metadata = SupportingPaneSceneStrategy.supportingPane() +
                    SupportingPaneSceneStrategy.preferredPaneSize(
                        height = if (isTabletop) 0.5f else Float.NaN,
                    ) +
                    SupportingPaneSceneStrategy.paneAnimation(
                        enterTransition = if (isTabletop) {
                            slideInVertically(tween(300)) { it }
                        } else {
                            slideInHorizontally(tween(300)) { it }
                        },
                        exitTransition = if (isTabletop) {
                            slideOutVertically(tween(300)) { it }
                        } else {
                            slideOutHorizontally(tween(300)) { it }
                        },
                        boundsAnimationSpec = tween(300),
                    ),
            ) { destination ->
                val task = uiState.tasks.firstOrNull { it.id == destination.taskId }

                Column(
                    modifier = if (isTabletop) {
                        Modifier.consumeWindowInsets(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
                        )
                    } else {
                        Modifier
                    },
                ) {
                    if (isTabletop) HorizontalDivider()
                    if (task == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        TaskDetailsScreen(
                            taskId = task.code,
                            title = task.title,
                            description = task.description,
                            assignee = task.assignee?.name,
                            status = stringResource(task.status.labelResource()),
                            statusTone = task.status.tone(),
                            onClose = navigationState::closeTask,
                            isSupportingPane = supportsSideBySide || isTabletop,
                        )
                    }
                }
            }
        },
    )
}

private fun TaskStatus.labelResource() = when (this) {
    TaskStatus.TODO -> R.string.task_status_todo
    TaskStatus.IN_PROGRESS -> R.string.task_status_in_progress
    TaskStatus.DONE -> R.string.task_status_done
}

private fun TaskStatus.tone() = when (this) {
    TaskStatus.TODO -> TaskFlowStatusTone.Neutral
    TaskStatus.IN_PROGRESS -> TaskFlowStatusTone.Information
    TaskStatus.DONE -> TaskFlowStatusTone.Success
}
