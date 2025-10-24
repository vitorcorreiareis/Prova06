package com.example.prova06.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ImobiColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceWhite,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = NavyBlue,
    secondary = NavyBlue,
    onSecondary = SurfaceWhite,
    secondaryContainer = Color(0xFFE8EEF9),
    onSecondaryContainer = NavyBlue,
    tertiary = LightBlue,
    background = BackgroundGray,
    onBackground = DarkGray,
    surface = SurfaceWhite,
    onSurface = DarkGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray,
    error = ErrorRed,
    onError = SurfaceWhite,
    outline = CardBorder,
)

@Composable
fun ImobiControlTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ImobiColorScheme,
        typography = ImobiTypography,
        content = content
    )
}

@Composable
fun Prova06Theme(content: @Composable () -> Unit) = ImobiControlTheme(content)
