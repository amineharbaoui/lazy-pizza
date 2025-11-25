package com.example.lazypizza.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.designsystem.R
import com.example.designsystem.components.DsNavigationBar
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet

private val TOP_LEVEL_ROUTES: List<Route> = listOf(Route.TopLevel.Menu, Route.TopLevel.Cart, Route.TopLevel.History)

@Composable
fun BottomBar(
    currentRoute: Route,
    onRouteSelect: (Route) -> Unit,
) {
    DsNavigationBar.Bottom(
        menuItems = TOP_LEVEL_ROUTES.map { route ->
            DsNavigationBar.NavItem(
                icon = painterResource(
                    when (route) {
                        Route.TopLevel.Menu -> R.drawable.ic_menu
                        Route.TopLevel.Cart -> R.drawable.ic_cart
                        Route.TopLevel.History -> R.drawable.ic_history
                        else -> R.drawable.ic_menu
                    }
                ),
                badgeCount = if (route == Route.TopLevel.Cart) 3 else 0,
                label = route.title,
                isSelected = (route == currentRoute),
                onClick = { onRouteSelect(route) }
            )
        }
    )
}

@Composable
fun NavigationRail(
    currentRoute: Route,
    onRouteSelect: (Route) -> Unit,
) {
    DsNavigationBar.Rail(
        menuItems = TOP_LEVEL_ROUTES.map { route ->
            DsNavigationBar.NavItem(
                icon = painterResource(
                    when (route) {
                        Route.TopLevel.Menu -> R.drawable.ic_menu
                        Route.TopLevel.Cart -> R.drawable.ic_cart
                        Route.TopLevel.History -> R.drawable.ic_history
                        else -> R.drawable.ic_menu
                    }
                ),
                badgeCount = if (route == Route.TopLevel.Cart) 3 else 0,
                label = route.title,
                isSelected = (route == currentRoute),
                onClick = { onRouteSelect(route) }
            )
        }
    )
}

@PreviewPhoneTablet
@Composable
private fun BottomBarPreview() {
    LazyPizzaThemePreview {
        BottomBar(currentRoute = Route.TopLevel.Menu, onRouteSelect = {})
    }
}

@PreviewPhoneTablet
@Composable
private fun NavigationRailPreview() {
    LazyPizzaThemePreview {
        NavigationRail(currentRoute = Route.TopLevel.Cart, onRouteSelect = {})
    }
}