package br.com.goulart.taskflow.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = TaskFlowPrimary,
    onPrimary = TaskFlowOnPrimary,
    primaryContainer = TaskFlowPrimaryContainer,
    onPrimaryContainer = TaskFlowOnPrimaryContainer,

    secondary = TaskFlowSecondary,
    onSecondary = TaskFlowOnSecondary,
    secondaryContainer = TaskFlowSecondaryContainer,
    onSecondaryContainer = TaskFlowOnSecondaryContainer,

    tertiary = TaskFlowSecondary,
    onTertiary = TaskFlowOnSecondary,
    tertiaryContainer = TaskFlowSecondaryContainer,
    onTertiaryContainer = TaskFlowOnSecondaryContainer,

    background = TaskFlowBackground,
    onBackground = TaskFlowOnBackground,

    surface = TaskFlowSurface,
    onSurface = TaskFlowOnSurface,
    surfaceVariant = TaskFlowSurfaceVariant,
    onSurfaceVariant = TaskFlowOnSurfaceVariant,
    surfaceDim = TaskFlowSurfaceDim,
    surfaceBright = TaskFlowSurfaceBright,
    surfaceContainerLowest = TaskFlowSurfaceContainerLowest,
    surfaceContainerLow = TaskFlowSurfaceContainerLow,
    surfaceContainer = TaskFlowSurfaceContainer,
    surfaceContainerHigh = TaskFlowSurfaceContainerHigh,
    surfaceContainerHighest = TaskFlowSurfaceContainerHighest,
    inverseSurface = TaskFlowSurfaceDark,
    inverseOnSurface = TaskFlowOnSurfaceDark,
    inversePrimary = TaskFlowPrimaryDark,

    outline = TaskFlowOutline,
    outlineVariant = TaskFlowOutlineVariant,

    error = TaskFlowError,
    onError = TaskFlowOnError,
    errorContainer = TaskFlowErrorContainer,
    onErrorContainer = TaskFlowOnErrorContainer,
)

private val DarkColorScheme = darkColorScheme(
    primary = TaskFlowPrimaryDark,
    onPrimary = TaskFlowOnPrimaryDark,
    primaryContainer = TaskFlowPrimaryContainerDark,
    onPrimaryContainer = TaskFlowOnPrimaryContainerDark,

    secondary = TaskFlowSecondaryDark,
    onSecondary = TaskFlowOnSecondaryDark,
    secondaryContainer = TaskFlowSecondaryContainerDark,
    onSecondaryContainer = TaskFlowOnSecondaryContainerDark,

    tertiary = TaskFlowSecondaryDark,
    onTertiary = TaskFlowOnSecondaryDark,
    tertiaryContainer = TaskFlowSecondaryContainerDark,
    onTertiaryContainer = TaskFlowOnSecondaryContainerDark,

    background = TaskFlowBackgroundDark,
    onBackground = TaskFlowOnBackgroundDark,

    surface = TaskFlowSurfaceDark,
    onSurface = TaskFlowOnSurfaceDark,
    surfaceVariant = TaskFlowSurfaceVariantDark,
    onSurfaceVariant = TaskFlowOnSurfaceVariantDark,
    surfaceDim = TaskFlowSurfaceDimDark,
    surfaceBright = TaskFlowSurfaceBrightDark,
    surfaceContainerLowest = TaskFlowSurfaceContainerLowestDark,
    surfaceContainerLow = TaskFlowSurfaceContainerLowDark,
    surfaceContainer = TaskFlowSurfaceContainerDark,
    surfaceContainerHigh = TaskFlowSurfaceContainerHighDark,
    surfaceContainerHighest = TaskFlowSurfaceContainerHighestDark,
    inverseSurface = TaskFlowSurface,
    inverseOnSurface = TaskFlowOnSurface,
    inversePrimary = TaskFlowPrimary,

    outline = TaskFlowOutlineDark,
    outlineVariant = TaskFlowOutlineVariantDark,

    error = TaskFlowErrorDark,
    onError = TaskFlowOnErrorDark,
    errorContainer = TaskFlowErrorContainerDark,
    onErrorContainer = TaskFlowOnErrorContainerDark,
)

@Composable
fun TaskFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
