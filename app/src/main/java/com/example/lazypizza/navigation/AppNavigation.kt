package com.example.lazypizza.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lazypizza.features.cart.presentation.CartScreen
import com.example.lazypizza.features.detail.presentation.DetailScreen
import com.example.lazypizza.features.history.presentation.HistoryScreen
import com.example.lazypizza.features.home.presentation.HomeScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.Menu
    ) {
        composable<Route.Home.Menu> {
            HomeScreen(
                innerPadding = innerPadding,
                onProductClick = { productId ->
                    navController.navigate(Route.Detail(productId))
                }
            )
        }
        composable<Route.Detail> { backStackEntry ->
            DetailScreen(
                innerPadding = innerPadding,
                onBackClick = { navController.popBackStack() },
            )
        }
        composable<Route.Home.Cart> { backStackEntry ->
            CartScreen(innerPadding = innerPadding)
        }
        composable<Route.Home.History> { backStackEntry ->
            HistoryScreen(innerPadding = innerPadding)
        }
    }
}
