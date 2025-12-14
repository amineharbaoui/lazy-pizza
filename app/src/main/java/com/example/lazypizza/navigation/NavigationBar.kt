package com.example.lazypizza.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavKey
import com.example.designsystem.R
import com.example.designsystem.components.DsNavigationBar
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.history.presentation.HistoryRoute
import com.example.uilogin.cart.screen.CartRoute
import com.example.uilogin.pizzadetail.MenuRoute

val TopLevelRoutes: Set<NavKey> = setOf(MenuRoute, CartRoute, HistoryRoute)

fun NavKey.isTopLevel(): Boolean = this in TopLevelRoutes

@Composable
fun BottomBar(
    currentRoute: NavKey,
    onRouteSelect: (NavKey) -> Unit,
    badgeCount: Int,
) {
    DsNavigationBar.Bottom(
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
                isSelected = (currentRoute::class == destination::class),
                onClick = { onRouteSelect(destination) },
            )
        },
    )
}

private fun NavKey.toIcon(): Int = when (this) {
    MenuRoute -> R.drawable.menu
    CartRoute -> R.drawable.shopping_cart
    HistoryRoute -> R.drawable.history
    else -> R.drawable.menu
}

private fun NavKey.toLabel(): String = when (this) {
    MenuRoute -> "Menu"
    CartRoute -> "Cart"
    HistoryRoute -> "History"
    else -> ""
}

@PreviewPhoneTablet
@Composable
private fun BottomBarPreview() {
    LazyPizzaThemePreview {
        BottomBar(
            currentRoute = MenuRoute,
            onRouteSelect = {},
            badgeCount = 3,
        )
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
