package br.com.goulart.taskflow.designsystem.component.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.ui.theme.TaskFlowTheme

@Composable
fun TaskFlowNavigationRail(
    items: List<TaskFlowNavigationItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(240.dp)
            .windowInsetsPadding(
                WindowInsets.statusBars
                    .union(WindowInsets.navigationBars)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items.forEachIndexed { index, item ->
            TaskFlowNavigationRailItem(
                item = item,
                selected = selectedIndex == index,
                onClick = {
                    onItemClick(index)
                },
            )
        }
    }
}

@Composable
private fun TaskFlowNavigationRailItem(
    item: TaskFlowNavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationDrawerItem(
        label = {
            Text(text = item.label)
        },
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
            )
        },
        modifier = modifier,
    )
}

@Preview(
    name = "Navigation Rail",
    showBackground = true,
    heightDp = 700,
)
@Composable
private fun TaskFlowNavigationRailPreview() {
    TaskFlowTheme {
        TaskFlowNavigationRail(
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
