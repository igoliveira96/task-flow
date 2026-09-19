package br.com.goulart.taskflow.designsystem.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.goulart.taskflow.ui.theme.TaskFlowTheme

@Composable
fun TaskFlowNavigationBar(
    items: List<TaskFlowNavigationItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    onItemClick(index)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(text = item.label)
                },
            )
        }
    }
}

@Preview
@Composable
private fun TaskFlowNavigationBarPreview() {
    TaskFlowTheme {
        TaskFlowNavigationBar(
            items = listOf(
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
            ),
            selectedIndex = 1,
            onItemClick = {},
        )
    }
}
