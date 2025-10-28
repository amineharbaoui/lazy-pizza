package com.example.lazypizza.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.example.lazypizza.navigation.RootNavGraph

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val customNavSuiteType = with(adaptiveInfo) {
        if (
            windowSizeClass.isWidthAtLeastBreakpoint(
                widthDpBreakpoint = WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
            )
        ) {
            NavigationSuiteType.Companion.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }

    NavigationSuiteScaffold(
        layoutType = customNavSuiteType,
        navigationSuiteItems = navigationItems(navController)
    ) {
        RootNavGraph(
            navController = navController,
            innerPadding = WindowInsets.Companion.safeDrawing.only(
                sides = WindowInsetsSides.Companion.Top + WindowInsetsSides.Companion.Horizontal
            ).asPaddingValues()
        )
    }
}