package com.example.lazypizza.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.lazypizza.features.cart.presentation.CartScreen
import com.example.lazypizza.features.detail.presentation.DetailScreen
import com.example.lazypizza.features.history.presentation.HistoryScreen
import com.example.lazypizza.features.home.presentation.HomeScreen

@Composable
fun RootNavGraph(
    backStack: SnapshotStateList<Any>,
    onBack: () -> Unit,
    innerPadding: PaddingValues,
) {
    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<Route.TopLevel.Menu> {
                HomeScreen(
                    innerPadding = innerPadding,
                    onProductClick = { productId ->
//                        navController.navigate()
                        backStack.add(Route.Detail(productId))
                    }
                )
            }
            entry<Route.Detail> { backStackEntry ->
                DetailScreen(
                    innerPadding = innerPadding,
                    onBackClick = onBack,
                )
            }
            entry<Route.TopLevel.Cart> { backStackEntry ->
                CartScreen(innerPadding = innerPadding)
            }
            entry<Route.TopLevel.History> { backStackEntry ->
                HistoryScreen(innerPadding = innerPadding)
            }
        }
    )
}
