package br.com.goulart.taskflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.ui.graphics.vector.ImageVector

enum class TaskFlowDestination(
    val label: String,
    val icon: ImageVector,
) {
    HOME(
        label = "Home",
        icon = Icons.Outlined.Home,
    ),
    PROJECTS(
        label = "Projects",
        icon = Icons.Outlined.ViewKanban,
    ),
    MY_TASKS(
        label = "My tasks",
        icon = Icons.Outlined.CheckCircle,
    ),
    SETTINGS(
        label = "Settings",
        icon = Icons.Outlined.Settings,
    ),
}
