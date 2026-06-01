package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val AppColorScheme = darkColorScheme(
    primary = DevPrimary,
    secondary = DevSecondary,
    background = DevBackground,
    surface = DevSurface,
    surfaceVariant = DevSurfaceVariant,
    error = DevError,
    onPrimary = DevBackground,
    onSecondary = DevBackground,
    onBackground = DevText,
    onSurface = DevText,
    onSurfaceVariant = DevTextMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for the console aesthetic
    dynamicColor: Boolean = false, // Force our custom theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
