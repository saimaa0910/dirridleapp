package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp

private val palette = DiriddlePalette()

private val DiriddleDarkColorScheme = darkColorScheme(
    primary = palette.accent,
    onPrimary = palette.background,
    primaryContainer = palette.accentMuted,
    onPrimaryContainer = palette.accent,
    secondary = palette.info,
    onSecondary = palette.background,
    background = palette.background,
    onBackground = palette.textPrimary,
    surface = palette.surface,
    onSurface = palette.textPrimary,
    surfaceVariant = palette.card,
    onSurfaceVariant = palette.textSecondary,
    outline = palette.border,
    outlineVariant = palette.borderSubtle,
    error = palette.danger,
    onError = palette.background,
    errorContainer = palette.dangerMuted,
    onErrorContainer = palette.danger,
)

private val DiriddleShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

object DiriddleTheme {
    val colors: DiriddlePalette
        @Composable
        @ReadOnlyComposable
        get() = LocalDiriddlePalette.current
}

@Composable
fun DiriddleTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDiriddlePalette provides palette) {
        MaterialTheme(
            colorScheme = DiriddleDarkColorScheme,
            typography = DiriddleTypography,
            shapes = DiriddleShapes,
            content = content,
        )
    }
}
