package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = ObsidianVoid,
    primaryContainer = CyberCobaltDeep,
    onPrimaryContainer = TextPrimary,
    secondary = CyberCobalt,
    onSecondary = Color.White,
    secondaryContainer = HoloCardElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = CyberMint,
    onTertiary = ObsidianVoid,
    tertiaryContainer = Color(0xFF0A2E20),
    onTertiaryContainer = CyberMint,
    background = ObsidianVoid,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = HoloCardBg,
    onSurfaceVariant = TextSecondary,
    outline = HoloBorder,
    outlineVariant = Color(0xFF152544),
    error = CyberCoral,
    onError = ObsidianVoid,
    errorContainer = Color(0xFF3A1219),
    onErrorContainer = Color(0xFFFFB4AB)
)

private val CyberLightColorScheme = lightColorScheme(
    primary = LightCyberPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDEEFF),
    onPrimaryContainer = LightTextPrimary,
    secondary = LightCyberSecondary,
    onSecondary = Color.White,
    secondaryContainer = LightCyberCard,
    onSecondaryContainer = LightTextPrimary,
    tertiary = Color(0xFF007A43),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCFF5E2),
    onTertiaryContainer = Color(0xFF00381D),
    background = LightCyberBg,
    onBackground = LightTextPrimary,
    surface = LightCyberSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCyberCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCyberOutline,
    outlineVariant = Color(0xFFD5E2F0),
    error = Color(0xFFC62828),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val CyberHighContrastColorScheme = darkColorScheme(
    primary = HighContrastCyan,
    onPrimary = HighContrastBg,
    primaryContainer = Color(0xFF003B4D),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF82B1FF),
    onSecondary = HighContrastBg,
    secondaryContainer = HighContrastSurface,
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF69F0AE),
    onTertiary = HighContrastBg,
    background = HighContrastBg,
    onBackground = Color.White,
    surface = HighContrastSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF101828),
    onSurfaceVariant = Color(0xFFE0ECFF),
    outline = HighContrastBorder,
    outlineVariant = HighContrastBorder,
    error = Color(0xFFFF6E6E),
    onError = HighContrastBg
)

val CyberShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun CyberTvBoxTheme(
    darkTheme: Boolean = true,
    highContrast: Boolean = false,
    largeText: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast -> CyberHighContrastColorScheme
        darkTheme -> CyberDarkColorScheme
        else -> CyberLightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = cyberTypography(largeText = largeText),
        shapes = CyberShapes,
        content = content
    )
}
