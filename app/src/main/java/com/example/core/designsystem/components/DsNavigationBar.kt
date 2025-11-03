package com.example.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.lazypizza.R
import com.example.lazypizza.navigation.Route

object DsNavigationBar {

    @Composable
    fun Bottom(menuItems: List<NavItem>) {
        Surface(
            shadowElevation = 12.dp,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                menuItems.forEach { item ->
                    NavigationBarItem(
                        selected = item.isSelected,
                        onClick = { if (item.isSelected.not()) item.onClick(item.route) },
                        icon = { NavIcon(item.icon, item.route.title, item.badgeCount) },
                        label = { Text(text = item.route.title) },
                        colors = NavigationBarItemColors(
                            selectedIconColor = AppColors.Primary,
                            selectedTextColor = AppColors.TextPrimary,
                            selectedIndicatorColor = AppColors.Primary_8,
                            unselectedIconColor = AppColors.TextSecondary,
                            unselectedTextColor = AppColors.TextSecondary,
                            disabledIconColor = AppColors.TextSecondary,
                            disabledTextColor = AppColors.TextSecondary
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun Rail(menuItems: List<NavItem>) {
        NavigationRail(
            containerColor = Color.Transparent,
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                menuItems.forEach { item ->
                    NavigationRailItem(
                        selected = item.isSelected,
                        onClick = { if (item.isSelected.not()) item.onClick(item.route) },
                        icon = { NavIcon(item.icon, item.route.title, item.badgeCount) },
                        label = { Text(item.route.title) },
                        colors = NavigationRailItemColors(
                            selectedIconColor = AppColors.Primary,
                            selectedTextColor = AppColors.TextPrimary,
                            selectedIndicatorColor = AppColors.Primary_8,
                            unselectedIconColor = AppColors.TextSecondary,
                            unselectedTextColor = AppColors.TextSecondary,
                            disabledIconColor = AppColors.TextSecondary,
                            disabledTextColor = AppColors.TextSecondary
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun NavIcon(
        painter: Painter,
        label: String,
        badgeCount: Int,
    ) {
        val base = @Composable {
            Icon(painter = painter, contentDescription = label)
        }
        if (badgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = AppColors.Primary,
                        contentColor = AppColors.OnPrimary,
                        modifier = Modifier.offset(x = 10.dp, y = (-8).dp)
                    ) { Text(text = badgeCount.toString()) }
                }
            ) { base() }
        } else {
            base()
        }
    }

    data class NavItem(
        val icon: Painter,
        val badgeCount: Int = 0,
        val route: Route,
        val isSelected: Boolean,
        val onClick: (Route) -> Unit,
    )
}

@PreviewPhoneTablet
@Composable
private fun DsNavigationBarPreview() {
    LazyPizzaThemePreview {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
        ) {
            DsNavigationBar.Bottom(
                menuItems = listOf(
                    DsNavigationBar.NavItem(
                        icon = painterResource(R.drawable.ic_menu),
                        badgeCount = 0,
                        route = Route.Home.Menu,
                        isSelected = true,
                        onClick = {}
                    ),
                    DsNavigationBar.NavItem(
                        icon = painterResource(R.drawable.ic_cart),
                        badgeCount = 3,
                        route = Route.Home.Cart,
                        isSelected = false,
                        onClick = {}
                    ),
                    DsNavigationBar.NavItem(
                        icon = painterResource(R.drawable.ic_history),
                        badgeCount = 0,
                        route = Route.Home.History,
                        isSelected = false,
                        onClick = {}
                    )
                )
            )
            Spacer(Modifier.height(16.dp))
            DsNavigationBar.Rail(
                menuItems = listOf(
                    DsNavigationBar.NavItem(
                        icon = painterResource(R.drawable.ic_menu),
                        badgeCount = 0,
                        route = Route.Home.Menu,
                        isSelected = true,
                        onClick = {}
                    ),
                    DsNavigationBar.NavItem(
                        icon = painterResource(R.drawable.ic_cart),
                        badgeCount = 3,
                        route = Route.Home.Cart,
                        isSelected = false,
                        onClick = {}
                    ),
                    DsNavigationBar.NavItem(
                        icon = painterResource(R.drawable.ic_history),
                        badgeCount = 0,
                        route = Route.Home.History,
                        isSelected = false,
                        onClick = {}
                    )
                )
            )
        }
    }
}

