package com.example.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {
    val TextPrimary = Color(0xFF03131F)
    val TextPrimary_8 = Color(0x1403131F)
    val TextSecondary = Color(0xFF627686)
    val TextSecondary_8 = Color(0x14627686)
    val TextOnPrimary = Color(0xFFFFFFFF)
    val TextOnPrimary_12 = Color(0x1FFFFFFF)

    //    val Bg = Color(0xFFFAFBFC)
    val Bg = Color(0xFFFFFFFF)

    val SurfaceHigher = Color(0xFFFFFFFF)
    val SurfaceHighest = Color(0xFFF0F3F6)

    val Overlay = Color(0x8003131F)
    val Outline = Color(0xFFE6E7ED)
    val Outline_50 = Color(0x80E6E7ED)

    val Primary = Color(0xFFF36B50)
    val Primary_8 = Color(0x14F36B50)
    val OnPrimary = Color(0xFFFFFFFF)

    val PrimaryGradientStart = Color(0xFFF36B50)
    val PrimaryGradientEnd = Color(0xFFF9966F)

    val PrimaryGradientBrush: Brush
        get() = Brush.linearGradient(colors = listOf(PrimaryGradientStart, PrimaryGradientEnd))

    val PrimaryShadow = Color(0x40F36B50)

    val Success = Color(0xFF29A55A)
    val Warning = Color(0xFFF5A623)
    val Error = Color(0xffff0000)
}
