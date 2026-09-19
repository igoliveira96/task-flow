package br.com.goulart.taskflow.designsystem.component.header

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.goulart.taskflow.designsystem.theme.TaskFlowTheme

@Composable
fun TaskFlowPageHeader(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    overline: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (overline != null) {
            Text(
                text = overline,
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "Page header - Light", showBackground = true, widthDp = 360)
@Preview(
    name = "Page header - Dark",
    showBackground = true,
    widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TaskFlowPageHeaderPreview() {
    TaskFlowTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TaskFlowPageHeader(
                title = "Home",
                description = "TaskFlow · Team board",
                modifier = Modifier.padding(20.dp),
            )
        }
    }
}
