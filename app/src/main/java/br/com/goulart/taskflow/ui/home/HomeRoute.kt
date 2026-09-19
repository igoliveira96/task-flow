package br.com.goulart.taskflow.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import br.com.goulart.taskflow.ui.home.navigation.HomeDestination
import br.com.goulart.taskflow.ui.home.navigation.HomeNavigationState
import br.com.goulart.taskflow.ui.home.navigation.rememberHomeNavigationState
import br.com.goulart.taskflow.ui.taskdetails.TaskDetailsScreen

private const val BoardToDetailsPreferredWidthRatio = 2
private val PaneSpacing = 8.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    navigationState: HomeNavigationState = rememberHomeNavigationState(),
) {
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
                        width = directive.defaultPanePreferredWidth * BoardToDetailsPreferredWidthRatio,
                    ) +
                    SupportingPaneSceneStrategy.paneAnimation(boundsAnimationSpec = tween(300)),
            ) {
                HomeScreen(
                    modifier = if (isTabletop && hasSelectedTask) {
                        Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                    } else {
                        Modifier
                    },
                    selectedTaskId = navigationState.selectedTaskId,
                    isPane = supportsSideBySide,
                    onTaskClick = navigationState::openTask,
                    singleColumn = !windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
                        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                    ),
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
                val column = homeMockColumns.first { column ->
                    column.tasks.any { it.id == destination.taskId }
                }
                val task = column.tasks.first { it.id == destination.taskId }

                Column(
                    modifier = if (isTabletop) {
                        Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                    } else {
                        Modifier
                    },
                ) {
                    if (isTabletop) HorizontalDivider()
                    TaskDetailsScreen(
                        taskId = task.id,
                        title = task.title,
                        description = task.description,
                        assignee = task.assignee,
                        status = column.title,
                        statusTone = column.tone,
                        onClose = navigationState::closeTask,
                        isSupportingPane = supportsSideBySide,
                    )
                }
            }
        },
    )
}
