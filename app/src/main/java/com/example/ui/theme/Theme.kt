package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RadarCyanGlow,
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFCEE5FF),
    secondary = RadarCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFCBD5E1),
    tertiary = MarketingAmber,
    onTertiary = Color.Black,
    background = RadarDarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = RadarDarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = RadarDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = RadarDarkBorder,
    error = StatusExpiredRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = RadarCyan,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = RadarBlue,
    onSecondary = Color.White,
    secondaryContainer = RadarLightSurfaceVariant,
    onSecondaryContainer = Color(0xFF334155),
    tertiary = MarketingAmber,
    onTertiary = Color.Black,
    background = RadarLightBackground,
    onBackground = Color(0xFF0F172A),
    surface = RadarLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = RadarLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = RadarLightBorder,
    error = StatusExpiredRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek tech dark mode for radar dashboard
    dynamicColor: Boolean = false, // Keep consistent radar branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
