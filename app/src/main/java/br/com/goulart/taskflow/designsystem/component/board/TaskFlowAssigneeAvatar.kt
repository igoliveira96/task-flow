package br.com.goulart.taskflow.designsystem.component.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp

@Composable
fun TaskFlowAssigneeAvatar(name: String, modifier: Modifier = Modifier) {
    val initials = remember(name) {
        name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            .let { parts -> listOfNotNull(parts.firstOrNull(), parts.lastOrNull().takeIf { parts.size > 1 }) }
            .joinToString("") { it.take(1).uppercase() }
    }
    Surface(
        modifier = modifier.size(28.dp).clearAndSetSemantics { contentDescription = name },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = initials, style = MaterialTheme.typography.labelSmall)
        }
    }
}
