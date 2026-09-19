package br.com.goulart.taskflow.ui.home

import br.com.goulart.taskflow.designsystem.component.board.TaskFlowStatusTone

internal data class HomeTask(
    val id: String,
    val title: String,
    val description: String,
    val assignee: String,
)

internal data class HomeBoardColumn(
    val id: String,
    val title: String,
    val tasks: List<HomeTask>,
    val tone: TaskFlowStatusTone = TaskFlowStatusTone.Neutral,
)

internal val homeMockColumns = listOf(
    HomeBoardColumn(
        id = "todo",
        title = "To do",
        tasks = listOf(
            HomeTask("TF-101", "Create project overview", "Show project progress and upcoming milestones.", "Ana Silva"),
            HomeTask("TF-102", "Add task filters", "Filter the board by assignee and priority.", "Bruno Costa"),
            HomeTask("TF-103", "Design task details", "Organize descriptions, comments and attachments.", "Carla Santos"),
            HomeTask("TF-104", "Plan notifications", "Define reminders for upcoming due dates.", "Ana Silva"),
            HomeTask("TF-105", "Add project search", "Find projects by name or description.", "Bruno Costa"),
        ),
    ),
    HomeBoardColumn(
        id = "in_progress",
        title = "In progress",
        tone = TaskFlowStatusTone.Information,
        tasks = listOf(
            HomeTask("TF-106", "Build the Home board", "Display tasks grouped by their current status.", "Carla Santos"),
            HomeTask("TF-107", "Review navigation", "Check the bottom bar and sidebar on different screens.", "Ana Silva"),
            HomeTask("TF-108", "Prepare the design system", "Document reusable components and theme colors.", "Bruno Costa"),
        ),
    ),
    HomeBoardColumn(
        id = "done",
        title = "Done",
        tone = TaskFlowStatusTone.Success,
        tasks = listOf(
            HomeTask("TF-109", "Set up the project", "Configure the Android app and Compose dependencies.", "Bruno Costa"),
            HomeTask("TF-110", "Define the app theme", "Create blue palettes for light and dark themes.", "Carla Santos"),
        ),
    ),
)
