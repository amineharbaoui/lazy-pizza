package com.example.lazypizza.main

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.core.designsystem.theme.AppColors
import com.example.lazypizza.R
import com.example.lazypizza.navigation.Route

@Composable
fun navigationItems(
    navController: NavHostController,
): NavigationSuiteScope.() -> Unit {
    var selectedItem by remember { mutableStateOf<Route>(Route.Menu) }
    val itemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppColors.Primary,
            selectedTextColor = AppColors.TextPrimary,
            indicatorColor = AppColors.Primary_8,
            unselectedIconColor = AppColors.TextSecondary,
            unselectedTextColor = AppColors.TextSecondary,
            disabledIconColor = AppColors.TextSecondary,
            disabledTextColor = AppColors.TextSecondary
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = AppColors.Primary,
            selectedTextColor = AppColors.TextPrimary,
            indicatorColor = AppColors.Primary_8,
            unselectedIconColor = AppColors.TextSecondary,
            unselectedTextColor = AppColors.TextSecondary,
            disabledIconColor = AppColors.TextSecondary,
            disabledTextColor = AppColors.TextSecondary
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = AppColors.Primary,
            selectedTextColor = AppColors.TextPrimary,
            selectedContainerColor = AppColors.Primary_8,
            unselectedIconColor = AppColors.TextSecondary,
            unselectedTextColor = AppColors.TextSecondary,
            unselectedContainerColor = Color.Companion.Transparent,
        ),
    )
    return {
        item(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu",
                    tint = if (selectedItem == Route.Menu) AppColors.Primary else AppColors.TextSecondary,
                    modifier = Modifier.Companion.size(20.dp)
                )
            },
            label = { Text("Menu") },
            selected = selectedItem == Route.Menu,
            onClick = {
                selectedItem = Route.Menu
                navController.navigate(Route.Menu) { launchSingleTop = true }
            },
            colors = itemColors,
        )

        item(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "Cart",
                    tint = if (selectedItem == Route.Cart) AppColors.Primary else AppColors.TextSecondary,
                    modifier = Modifier.Companion.size(24.dp)
                )
            },
            badge = {
                Badge(
                    containerColor = AppColors.Primary,
                    contentColor = AppColors.OnPrimary,
                    modifier = Modifier.Companion
                        .offset(x = 10.dp, y = (-8).dp)
                ) { Text("3") }
            },
            label = { Text("Cart") },
            selected = selectedItem == Route.Cart,
            onClick = {
                selectedItem = Route.Cart
                navController.navigate(Route.Cart) { launchSingleTop = true }
            },
            colors = itemColors,
        )

        item(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = "History",
                    tint = if (selectedItem == Route.History) AppColors.Primary else AppColors.TextSecondary,
                    modifier = Modifier.Companion.size(24.dp)
                )
            },
            label = { Text("History") },
            selected = selectedItem == Route.History,
            onClick = {
                selectedItem = Route.History
                navController.navigate(Route.History) { launchSingleTop = true }
            },
            colors = itemColors,
        )
    }
}