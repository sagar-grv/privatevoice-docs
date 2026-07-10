package com.privatevoicedocs.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF365A50),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E8E0),
    onPrimaryContainer = Color(0xFF0E2D26),
    secondary = Color(0xFF53655E),
    background = Color(0xFFF8FAF8),
    surface = Color(0xFFF8FAF8),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB7CCC4),
    onPrimary = Color(0xFF20372F),
    primaryContainer = Color(0xFF354E46),
    onPrimaryContainer = Color(0xFFD3E8E0),
    secondary = Color(0xFFBAC9C2),
)

@Composable
fun PrivateVoiceDocsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
