package com.example.lazypizza.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.designsystem.components.DsNavigationBar
import com.example.core.designsystem.utils.isWideLayout
import com.example.lazypizza.R
import com.example.lazypizza.navigation.RootNavGraph
import com.example.lazypizza.navigation.Route

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Your own size check (e.g., from WindowSizeClass or your util)
    val isWide = isWideLayout()

    // Observe current destination
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    // Show nav chrome only on top-level destinations
    val showNav = destination.isTopLevel()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (showNav && !isWide) {
                DsNavigationBar.Bottom(
                    menuItems = buildMenuItems(destination, navController)
                )
            }
        }
    ) { innerPadding ->
        Row(Modifier.padding(innerPadding)) {
            if (showNav && isWide) {
                DsNavigationBar.Rail(
                    menuItems = buildMenuItems(destination, navController)
                )
            }

            // App content
            Box(Modifier.weight(1f)) {
                RootNavGraph(
                    navController = navController,
                    // We already consumed safe top+horizontal insets via contentWindowInsets.
                    // Bottom is handled by Scaffold when bottomBar is present.
                    innerPadding = PaddingValues(0.dp)
                )
            }
        }
    }
}

/* ---------------- helpers ---------------- */

private fun NavDestination?.isTopLevel(): Boolean =
    this?.hierarchy?.any { dest ->
        dest.hasRoute<Route.Home.Menu>() ||
                dest.hasRoute<Route.Home.Cart>() ||
                dest.hasRoute<Route.Home.History>()
    } == true

@Composable
private fun buildMenuItems(
    currentDest: NavDestination?,
    navController: NavHostController,
): List<DsNavigationBar.NavItem> {

    fun navigateTo(route: Route) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val onMenu = currentDest?.hierarchy?.any { it.hasRoute<Route.Home.Menu>() } == true
    val onCart = currentDest?.hierarchy?.any { it.hasRoute<Route.Home.Cart>() } == true
    val onHistory = currentDest?.hierarchy?.any { it.hasRoute<Route.Home.History>() } == true

    return listOf(
        DsNavigationBar.NavItem(
            icon = painterResource(R.drawable.ic_menu),
            badgeCount = 0,
            route = Route.Home.Menu,
            isSelected = onMenu,
            onClick = { if (!onMenu) navigateTo(it) }
        ),
        DsNavigationBar.NavItem(
            icon = painterResource(R.drawable.ic_cart),
            badgeCount = 3, // bind to your cart count state
            route = Route.Home.Cart,
            isSelected = onCart,
            onClick = { if (!onCart) navigateTo(it) }
        ),
        DsNavigationBar.NavItem(
            icon = painterResource(R.drawable.ic_history),
            badgeCount = 0,
            route = Route.Home.History,
            isSelected = onHistory,
            onClick = { if (!onHistory) navigateTo(it) }
        )
    )
}