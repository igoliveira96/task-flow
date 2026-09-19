package br.com.goulart.taskflow.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import br.com.goulart.taskflow.designsystem.component.navigation.TaskFlowNavigationItem
import br.com.goulart.taskflow.designsystem.component.navigation.TaskFlowNavigationSuite
import br.com.goulart.taskflow.designsystem.component.navigation.TaskFlowNavigationType
import br.com.goulart.taskflow.ui.home.HomeRoute
import br.com.goulart.taskflow.ui.home.navigation.rememberHomeNavigationState
import br.com.goulart.taskflow.ui.projects.ProjectsRoute

@Composable
fun TaskFlowApp(
    modifier: Modifier = Modifier,
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass

    val navigationType =
        navigationType(windowSizeClass)

    var selectedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val homeNavigationState = rememberHomeNavigationState()
    val homeStateHolder = rememberSaveableStateHolder()

    val navigationItems = remember {
        listOf(
            TaskFlowNavigationItem(
                label = "Home",
                icon = Icons.Outlined.Home,
            ),
            TaskFlowNavigationItem(
                label = "Projects",
                icon = Icons.Outlined.ViewKanban,
            ),
            TaskFlowNavigationItem(
                label = "My tasks",
                icon = Icons.Outlined.CheckCircle,
            ),
            TaskFlowNavigationItem(
                label = "Settings",
                icon = Icons.Outlined.Settings,
            ),
        )
    }

    val content: @Composable () -> Unit = {
        when (selectedIndex) {
            0 -> {
                homeStateHolder.SaveableStateProvider(key = "home") {
                    HomeRoute(navigationState = homeNavigationState)
                }
            }
            1 -> {
                ProjectsRoute()
            }
            else -> {
                Text(
                    text = navigationItems[selectedIndex].label,
                )
            }
        }
    }

    val showHomeWithoutNavigation = selectedIndex == 0 && (
        windowAdaptiveInfo.windowPosture.isTabletop ||
            (navigationType == TaskFlowNavigationType.BOTTOM_BAR &&
                homeNavigationState.selectedTaskId != null)
        )

    if (showHomeWithoutNavigation) {
        Box(modifier = modifier) { content() }
    } else {
        TaskFlowNavigationSuite(
            navigationType = navigationType,
            items = navigationItems,
            selectedIndex = selectedIndex,
            onItemClick = { index ->
                selectedIndex = index
                homeNavigationState.closeTask()
            },
            modifier = modifier,
            content = content,
        )
    }
}

private fun navigationType(
    windowSizeClass: WindowSizeClass,
): TaskFlowNavigationType {
    return if (
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        )
    ) {
        TaskFlowNavigationType.NAVIGATION_RAIL
    } else {
        TaskFlowNavigationType.BOTTOM_BAR
    }
}
