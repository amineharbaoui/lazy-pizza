package com.example.lazypizza.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {

    val TextPrimary = Color(0xFF03131F)
    val TextPrimary_8 = Color(0x14131f14)
    val TextSecondary = Color(0xFF627686)
    val TextSecondary_8 = Color(0x14627686)

    val Bg = Color(0xFFE6E7ED)
    val SurfaceHigher = Color(0xFFFFFFFF)
    val SurfaceHighest = Color(0xFFF0F3F6)


    val Outline = Color(0xFFE6E7ED)
    val Outline_50 = Color(0x80E6E7ED)

    val Primary = Color(0xFFF36B50)
    val Primary_8 = Color(0x14F36B50)
    val OnPrimary = Color(0xFFFFFFFF)


    val PrimaryGradientStart = Color(0xFFF36B50) // #F36B50
    val PrimaryGradientEnd = Color(0xFFF9966F) // #F9966F

    val PrimaryGradientBrush: Brush
        get() = Brush.linearGradient(
            listOf(PrimaryGradientStart, PrimaryGradientEnd)
        )

    val PrimaryShadow = Color(0x40F36B50)
}