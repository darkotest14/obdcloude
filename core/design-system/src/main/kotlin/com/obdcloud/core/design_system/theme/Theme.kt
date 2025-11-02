package com.obdcloud.core.design_system.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0B57D0),
    secondary = Color(0xFF4285F4),
    tertiary = Color(0xFF188038),
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFB3261E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    secondary = Color(0xFF4285F4),
    tertiary = Color(0xFF81C995),
    background = Color(0xFF1F1F1F),
    surface = Color(0xFF121212),
    error = Color(0xFFF28B82),
)

@Composable
fun OBDCloudTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    
    CompositionLocalProvider {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}