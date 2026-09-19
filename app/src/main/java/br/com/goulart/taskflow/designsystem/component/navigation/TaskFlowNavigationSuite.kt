package br.com.goulart.taskflow.designsystem.component.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class TaskFlowNavigationType {
    BOTTOM_BAR,
    NAVIGATION_RAIL,
}

@Composable
fun TaskFlowNavigationSuite(
    navigationType: TaskFlowNavigationType,
    items: List<TaskFlowNavigationItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when (navigationType) {
        TaskFlowNavigationType.BOTTOM_BAR -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                bottomBar = {
                    TaskFlowNavigationBar(
                        items = items,
                        selectedIndex = selectedIndex,
                        onItemClick = onItemClick,
                    )
                },
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    content()
                }
            }
        }

        TaskFlowNavigationType.NAVIGATION_RAIL -> {
            Row(
                modifier = modifier.fillMaxSize(),
            ) {
                TaskFlowNavigationRail(
                    items = items,
                    selectedIndex = selectedIndex,
                    onItemClick = onItemClick,
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                ) {
                    content()
                }
            }
        }
    }
}
