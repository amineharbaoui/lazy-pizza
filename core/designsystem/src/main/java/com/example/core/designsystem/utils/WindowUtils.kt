package com.example.core.designsystem.utils

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

@Composable
fun isWideLayout(minWidthDpBreakpoint: Int = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND): Boolean {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    return adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(minWidthDpBreakpoint)
}
