package com.example.africaquiz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DarkNavy,
    primaryContainer = GoldDark,
    secondary = AfricanGreen,
    onSecondary = TextWhite,
    secondaryContainer = AfricanGreenLight,
    background = DarkNavy,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextGray,
    error = WrongRed,
    onError = TextWhite
)

@Composable
fun AfricaQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
