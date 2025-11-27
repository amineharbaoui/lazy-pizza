package com.example.lazypizza.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavKey
import com.example.designsystem.R
import com.example.designsystem.components.DsNavigationBar
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.menu.presentation.CartRoute
import com.example.menu.presentation.HistoryRoute
import com.example.menu.presentation.MenuRoute

private val TOP_LEVEL_ROUTES: List<NavKey> = listOf(MenuRoute, CartRoute, HistoryRoute)

@Composable
fun BottomBar(
    currentRoute: NavKey,
    onRouteSelect: (NavKey) -> Unit,
) {
    DsNavigationBar.Bottom(
        menuItems = TOP_LEVEL_ROUTES.map { destination ->
            DsNavigationBar.NavItem(
                icon = painterResource(
                    when (destination) {
                        MenuRoute -> R.drawable.ic_menu
                        CartRoute -> R.drawable.ic_cart
                        HistoryRoute -> R.drawable.ic_history
                        else -> R.drawable.ic_menu
                    }
                ),
                badgeCount = if (destination == CartRoute) 3 else 0,
                label = when (destination) {
                    MenuRoute -> "Menu"
                    CartRoute -> "Cart"
                    HistoryRoute -> "History"
                    else -> ""
                },
                isSelected = (destination == currentRoute),
                onClick = { onRouteSelect(destination) }
            )
        }
    )
}

@Composable
fun NavigationRail(
    currentRoute: NavKey,
    onRouteSelect: (NavKey) -> Unit,
) {
    DsNavigationBar.Rail(
        menuItems = TOP_LEVEL_ROUTES.map { destination ->
            DsNavigationBar.NavItem(
                icon = painterResource(
                    when (destination) {
                        MenuRoute -> R.drawable.ic_menu
                        CartRoute -> R.drawable.ic_cart
                        HistoryRoute -> R.drawable.ic_history
                        else -> R.drawable.ic_menu
                    }
                ),
                badgeCount = if (destination == CartRoute) 3 else 0,
                label = when (destination) {
                    MenuRoute -> "Menu"
                    CartRoute -> "Cart"
                    HistoryRoute -> "History"
                    else -> ""
                },
                isSelected = (currentRoute::class == destination::class),
                onClick = { onRouteSelect(destination) }
            )
        }
    )
}

@PreviewPhoneTablet
@Composable
private fun BottomBarPreview() {
    LazyPizzaThemePreview {
        BottomBar(currentRoute = MenuRoute, onRouteSelect = {})
    }
}

@PreviewPhoneTablet
@Composable
private fun NavigationRailPreview() {
    LazyPizzaThemePreview {
        NavigationRail(currentRoute = CartRoute, onRouteSelect = {})
    }
}