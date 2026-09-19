package br.com.goulart.taskflow.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import br.com.goulart.taskflow.ui.home.navigation.HomeDestination
import br.com.goulart.taskflow.ui.home.navigation.HomeNavigationState
import br.com.goulart.taskflow.ui.home.navigation.rememberHomeNavigationState
import br.com.goulart.taskflow.ui.taskdetails.TaskDetailsScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    navigationState: HomeNavigationState = rememberHomeNavigationState(),
) {
    val adaptiveDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    val supportsSideBySide = adaptiveDirective.maxHorizontalPartitions > 1
    val directive = adaptiveDirective.copy(
        maxHorizontalPartitions = if (navigationState.selectedTaskId == null) {
            1
        } else {
            adaptiveDirective.maxHorizontalPartitions.coerceAtMost(2)
        },
        maxVerticalPartitions = 1,
        horizontalPartitionSpacerSize = 0.dp,
        verticalPartitionSpacerSize = 0.dp,
    )
    val supportingPaneStrategy = rememberSupportingPaneSceneStrategy<NavKey>(
        shouldHandleSinglePaneLayout = supportsSideBySide,
        directive = directive,
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
    )

    NavDisplay(
        backStack = navigationState.backStack,
        onBack = navigationState::closeTask,
        modifier = modifier,
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
                    SupportingPaneSceneStrategy.paneAnimation(boundsAnimationSpec = tween(300)),
            ) {
                HomeScreen(
                    selectedTaskId = navigationState.selectedTaskId,
                    onTaskClick = navigationState::openTask,
                )
            }
            entry<HomeDestination.TaskDetails>(
                metadata = SupportingPaneSceneStrategy.supportingPane() +
                    SupportingPaneSceneStrategy.paneAnimation(
                        enterTransition = slideInHorizontally(tween(300)) { it },
                        exitTransition = slideOutHorizontally(tween(300)) { it },
                        boundsAnimationSpec = tween(300),
                    ),
            ) { destination ->
                val task = homeMockColumns
                    .asSequence()
                    .flatMap { it.tasks }
                    .first { it.id == destination.taskId }

                TaskDetailsScreen(
                    taskId = task.id,
                    title = task.title,
                    description = task.description,
                    assignee = task.assignee,
                    onClose = navigationState::closeTask,
                    isSupportingPane = supportsSideBySide,
                )
            }
        },
    )
}
