package com.hendry.saku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography

val Typography = Typography()

private val LightColorScheme = lightColorScheme(
    primary = SakuNavy,
    onPrimary = SakuSurface,

    primaryContainer = SakuBlue,
    onPrimaryContainer = SakuSurface,

    secondary = SakuEmerald,
    onSecondary = SakuSurface,

    background = SakuBackground,
    onBackground = SakuTextPrimary,

    surface = SakuSurface,
    onSurface = SakuTextPrimary,

    surfaceVariant = ColorSurfaceVariant,
    onSurfaceVariant = SakuTextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = SakuEmerald,
    onPrimary = SakuNavy,

    primaryContainer = SakuNavy,
    onPrimaryContainer = SakuSurface,

    secondary = SakuEmerald,
    onSecondary = SakuNavy,

    background = SakuDarkBackground,
    onBackground = SakuSurface,

    surface = SakuDarkSurface,
    onSurface = SakuSurface
)

@Composable
fun SakuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}