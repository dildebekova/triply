package com.example.triply.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PinkLightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PinkSecondary,
    onPrimaryContainer = Color.White,
    secondary = HotPink,
    onSecondary = Color.White,
    tertiary = PinkTertiary,
    background = PinkBackground,
    surface = Color.White,
    onBackground = DarkGreyText,
    onSurface = DarkGreyText
)

private val PinkDarkColorScheme = darkColorScheme(
    primary = PinkPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = PinkSecondaryDark,
    onPrimaryContainer = Color.White,
    secondary = PinkPrimary,
    onSecondary = Color.Black,
    tertiary = PinkTertiaryDark,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = LightGreyText,
    onSurface = LightGreyText
)

@Composable
fun TriplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PinkDarkColorScheme else PinkLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
