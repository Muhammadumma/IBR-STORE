package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGlow,
    onPrimary = Color(0xFF06281E),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = InfoBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = EmeraldAccent,
    background = DarkCharcoalBg,
    onBackground = DarkTextPrimary,
    surface = DarkCharcoalSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCharcoalSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCharcoalBorder,
    error = DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = FintechEmerald,
    onPrimary = Color.White,
    primaryContainer = FintechEmeraldLight,
    onPrimaryContainer = FintechEmeraldDark,
    secondary = DeepCharcoal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5E7EB),
    onSecondaryContainer = DeepCharcoal,
    tertiary = WarningAmber,
    background = NeutralBackground,
    onBackground = DeepCharcoal,
    surface = PureWhite,
    onSurface = DeepCharcoal,
    surfaceVariant = NeutralSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = NeutralBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun VendoraTheme(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
