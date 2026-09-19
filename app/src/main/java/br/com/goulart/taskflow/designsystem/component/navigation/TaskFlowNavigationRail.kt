package br.com.goulart.taskflow.designsystem.component.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.R
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme

@Composable
fun TaskFlowNavigationRail(
    items: List<TaskFlowNavigationItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val railWidth by animateDpAsState(
        targetValue = if (expanded) 240.dp else 80.dp,
        animationSpec = tween(durationMillis = 300),
        label = "Rail width",
    )
    val toggleRotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        animationSpec = tween(durationMillis = 300),
        label = "Rail toggle rotation",
    )

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(railWidth),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items.forEachIndexed { index, item ->
                    TaskFlowNavigationRailItem(
                        item = item,
                        selected = selectedIndex == index,
                        expanded = expanded,
                        onClick = { onItemClick(index) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            IconButton(
                onClick = { onExpandedChange(!expanded) },
                modifier = Modifier
                    .align(Alignment.End)
                    .size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronLeft,
                    contentDescription = stringResource(
                        if (expanded) R.string.collapse_navigation else R.string.expand_navigation,
                    ),
                    modifier = Modifier.rotate(toggleRotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TaskFlowNavigationRailItem(
    item: TaskFlowNavigationItem,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
    ) {
        Row(
            modifier = Modifier.height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = if (expanded) null else item.label,
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(durationMillis = 150, delayMillis = 100)),
                exit = fadeOut(tween(durationMillis = 100)),
            ) {
                Text(
                    text = item.label,
                    modifier = Modifier.padding(end = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(
    name = "Navigation Rail",
    showBackground = true,
    heightDp = 700,
)
@Composable
private fun TaskFlowNavigationRailPreview(initiallyExpanded: Boolean = true) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
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
            expanded = expanded,
            onExpandedChange = { expanded = it },
        )
    }
}

@Preview(name = "Navigation Rail - Collapsed", showBackground = true, heightDp = 700)
@Composable
private fun TaskFlowNavigationRailCollapsedPreview() {
    TaskFlowNavigationRailPreview(initiallyExpanded = false)
}
