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
    onPrimaryContainer = DeepPink,
    secondary = HotPink,
    onSecondary = Color.White,
    tertiary = DeepPink,
    background = PinkBackground,
    surface = Color.White,
    onBackground = DarkGreyText,
    onSurface = DarkGreyText
)

@Composable
fun TriplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We stay with a feminine theme even in dark mode for now, or we could define a darker pink
    val colorScheme = PinkLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
