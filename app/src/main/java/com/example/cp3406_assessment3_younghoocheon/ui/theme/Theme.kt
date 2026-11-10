package com.example.cp3406_assessment3_younghoocheon.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.cp3406_assessment3_younghoocheon.data.AppSettings

val LocalReduceMotion = staticCompositionLocalOf { false }

private val DarkColors = darkColorScheme(
    primary = Lime,
    onPrimary = Ink,
    primaryContainer = Lime,
    onPrimaryContainer = Ink,
    secondary = Mint,
    onSecondary = Ink,
    secondaryContainer = Lime,
    onSecondaryContainer = Ink,
    background = Night,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = TextMuted,
    outline = OutlineDark,
    error = Danger,
)

private val HighContrastColors = DarkColors.copy(
    onSurfaceVariant = Color.White,
    surfaceVariant = Color(0xFF34403A),
    outline = Color(0xFF9AA59B),
)

private val BaseTypography = Typography()

private val AppTypography = BaseTypography.copy(
    displaySmall = BaseTypography.displaySmall.copy(fontWeight = FontWeight.Bold),
    headlineLarge = BaseTypography.headlineLarge.copy(fontWeight = FontWeight.Bold),
    headlineMedium = BaseTypography.headlineMedium.copy(fontWeight = FontWeight.Bold),
    headlineSmall = BaseTypography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = BaseTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = BaseTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = BaseTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = BaseTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
)

@Composable
fun MemoryDeskTheme(
    settings: AppSettings = AppSettings(),
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val scaledDensity = Density(
        density = density.density,
        fontScale = density.fontScale * settings.textScale,
    )
    val colors = if (settings.highContrast) HighContrastColors else DarkColors

    CompositionLocalProvider(
        LocalDensity provides scaledDensity,
        LocalReduceMotion provides settings.reduceMotion,
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}