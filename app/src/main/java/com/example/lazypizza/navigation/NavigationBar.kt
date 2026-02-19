package com.example.lazypizza.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavKey
import com.example.core.designsystem.R
import com.example.core.designsystem.components.DsNavigationBar
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet

val TopLevelRoutes: Set<NavKey> = setOf(MenuRoute, CartRoute, OrdersRoute)

fun NavKey.isTopLevel(): Boolean = this in TopLevelRoutes

@Composable
fun BottomBar(
    currentRoute: NavKey,
    onRouteSelect: (NavKey) -> Unit,
    badgeCount: Int,
) {
    Box(Modifier.navigationBarsPadding()) {
        DsNavigationBar.BubbleNotchBottomBar(
            menuItems = TopLevelRoutes.map { destination ->
                DsNavigationBar.NavItem(
                    icon = painterResource(destination.toIcon()),
                    badgeCount = if (destination == CartRoute) badgeCount else 0,
                    label = destination.toLabel(),
                    isSelected = (destination == currentRoute),
                    onClick = { onRouteSelect(destination) },
                )
            },
        )
    }
}

@Composable
fun NavigationRail(
    currentRoute: NavKey,
    onRouteSelect: (NavKey) -> Unit,
    badgeCount: Int,
) {
    DsNavigationBar.Rail(
        menuItems = TopLevelRoutes.map { destination ->
            DsNavigationBar.NavItem(
                icon = painterResource(destination.toIcon()),
                badgeCount = if (destination == CartRoute) badgeCount else 0,
                label = destination.toLabel(),
                isSelected = (destination == currentRoute),
                onClick = { onRouteSelect(destination) },
            )
        },
    )
}

private fun NavKey.toIcon(): Int = when (this) {
    MenuRoute -> R.drawable.menu
    CartRoute -> R.drawable.shopping_cart
    OrdersRoute -> R.drawable.orders
    else -> throw IllegalArgumentException("No Icon defined for NavKey: $this")
}

private fun NavKey.toLabel(): String = when (this) {
    MenuRoute -> "Menu"
    CartRoute -> "Cart"
    OrdersRoute -> "Orders"
    else -> throw IllegalArgumentException("No label defined for NavKey: $this")
}

@PreviewPhoneTablet
@Composable
private fun BottomBarPreview() {
    LazyPizzaThemePreview {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            BottomBar(
                currentRoute = MenuRoute,
                onRouteSelect = {},
                badgeCount = 3,
            )
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun NavigationRailPreview() {
    LazyPizzaThemePreview {
        NavigationRail(
            currentRoute = CartRoute,
            onRouteSelect = {},
            badgeCount = 0,
        )
    }
}
