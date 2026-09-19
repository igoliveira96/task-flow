package br.com.goulart.taskflow.ui.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeDestination : NavKey {
    @Serializable
    data object Board : HomeDestination

    @Serializable
    data class TaskDetails(val taskId: Long) : HomeDestination
}

@Stable
class HomeNavigationState internal constructor(
    val backStack: NavBackStack<NavKey>,
) {
    val selectedTaskId: Long?
        get() = (backStack.lastOrNull() as? HomeDestination.TaskDetails)?.taskId

    fun openTask(taskId: Long) {
        val destination = HomeDestination.TaskDetails(taskId)
        if (backStack.lastOrNull() == destination) return

        if (backStack.lastOrNull() is HomeDestination.TaskDetails) {
            backStack[backStack.lastIndex] = destination
        } else {
            backStack.add(destination)
        }
    }

    fun closeTask() {
        if (backStack.lastOrNull() is HomeDestination.TaskDetails) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}

@Composable
fun rememberHomeNavigationState(): HomeNavigationState {
    val backStack = rememberNavBackStack(HomeDestination.Board)
    return remember(backStack) { HomeNavigationState(backStack) }
}
