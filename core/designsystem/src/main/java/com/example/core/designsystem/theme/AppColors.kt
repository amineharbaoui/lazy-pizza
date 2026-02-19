package com.example.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {
    private val palette: AppPalette
        @Composable get() = LocalAppPalette.current

    val TextPrimary: Color
        @Composable get() = palette.textPrimary

    val TextPrimary_8: Color
        @Composable get() = palette.textPrimary8

    val TextSecondary: Color
        @Composable get() = palette.textSecondary

    val TextSecondary_8: Color
        @Composable get() = palette.textSecondary8

    val TextOnPrimary: Color
        @Composable get() = palette.textOnPrimary

    val TextOnPrimary_12: Color
        @Composable get() = palette.textOnPrimary12

    val Bg: Color
        @Composable get() = palette.bg

    val SurfaceHigher: Color
        @Composable get() = palette.surfaceHigher

    val SurfaceHighest: Color
        @Composable get() = palette.surfaceHighest

    val Overlay: Color
        @Composable get() = palette.overlay

    val Outline: Color
        @Composable get() = palette.outline

    val Outline_50: Color
        @Composable get() = palette.outline50

    val Primary: Color
        @Composable get() = palette.primary

    val Primary_8: Color
        @Composable get() = palette.primary8

    val OnPrimary: Color
        @Composable get() = palette.onPrimary

    val PrimaryGradientStart: Color
        @Composable get() = palette.gradientStart

    val PrimaryGradientEnd: Color
        @Composable get() = palette.gradientEnd

    val PrimaryGradientBrush: Brush
        @Composable get() = Brush.linearGradient(
            colors = listOf(palette.gradientStart, palette.gradientEnd),
        )

    val PrimaryShadow: Color
        @Composable get() = palette.primaryShadow

    val Success: Color
        @Composable get() = palette.success

    val Warning: Color
        @Composable get() = palette.warning

    val Error: Color
        @Composable get() = palette.error
}

@Immutable
data class AppPalette(
    val textPrimary: Color,
    val textPrimary8: Color,
    val textSecondary: Color,
    val textSecondary8: Color,
    val textOnPrimary: Color,
    val textOnPrimary12: Color,

    val bg: Color,
    val surfaceHigher: Color,
    val surfaceHighest: Color,

    val overlay: Color,
    val outline: Color,
    val outline50: Color,

    val primary: Color,
    val primary8: Color,
    val onPrimary: Color,

    val gradientStart: Color,
    val gradientEnd: Color,
    val primaryShadow: Color,

    val success: Color,
    val warning: Color,
    val error: Color,
)

val LightPalette = AppPalette(
    textPrimary = Color(0xFF03131F),
    textPrimary8 = Color(0x1403131F),
    textSecondary = Color(0xFF627686),
    textSecondary8 = Color(0x14627686),
    textOnPrimary = Color(0xFFFFFFFF),
    textOnPrimary12 = Color(0x1FFFFFFF),

    bg = Color(0xFFFAFBFC),
    surfaceHigher = Color(0xFFFFFFFF),
    surfaceHighest = Color(0xFFF0F3F6),

    overlay = Color(0x1F03131F),
    outline = Color(0xFFE6E7ED),
    outline50 = Color(0x80E6E7ED),

    primary = Color(0xFFF36B50),
    primary8 = Color(0x14F36B50),
    onPrimary = Color(0xFFFFFFFF),

    gradientStart = Color(0xFFF36B50),
    gradientEnd = Color(0xFFF9966F),
    primaryShadow = Color(0x40F36B50),

    success = Color(0xFF29A55A),
    warning = Color(0xFFF5A623),
    error = Color(0xFFFF0000),
)

val DarkPalette = AppPalette(
    textPrimary = Color(0xFFEAF1F7),
    textPrimary8 = Color(0x14EAF1F7),
    textSecondary = Color(0xFFA9B7C4),
    textSecondary8 = Color(0x14A9B7C4),
    textOnPrimary = Color(0xFFFFFFFF),
    textOnPrimary12 = Color(0x1FFFFFFF),

    bg = Color(0xFF0B141B),
    surfaceHigher = Color(0xFF101C25),
    surfaceHighest = Color(0xFF162634),

    overlay = Color(0x22FFFFFF),
    outline = Color(0xFF243543),
    outline50 = Color(0x80243543),

    primary = Color(0xFFF36B50),
    primary8 = Color(0x14F36B50),
    onPrimary = Color(0xFFFFFFFF),

    gradientStart = Color(0xFFF36B50),
    gradientEnd = Color(0xFFF9966F),
    primaryShadow = Color(0x40F36B50),

    success = Color(0xFF3AD47A),
    warning = Color(0xFFFFB74D),
    error = Color(0xFFFF4D4D),
)
