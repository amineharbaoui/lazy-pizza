package com.example.lazypizza.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.lazypizza.features.cart.presentation.CartScreen
import com.example.lazypizza.features.history.presentation.HistoryScreen
import com.example.menu.presentation.CartRoute
import com.example.menu.presentation.HistoryRoute
import com.example.menu.presentation.MenuRoute
import com.example.menu.presentation.PizzaDetailRoute
import com.example.menu.presentation.detail.PizzaDetailScreen
import com.example.menu.presentation.detail.PizzaDetailViewModel
import com.example.menu.presentation.menu.MenuScreen

@Composable
fun RootNavGraph(
    backStack: SnapshotStateList<NavKey>,
    onBack: () -> Unit,
    innerPadding: PaddingValues,
) {
    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<MenuRoute> {
                MenuScreen(
                    innerPadding = innerPadding,
                    onProductClick = { productId ->
                        backStack.add(PizzaDetailRoute(productId = productId))
                    }
                )
            }
            entry<PizzaDetailRoute> {
                val viewModel = hiltViewModel<PizzaDetailViewModel, PizzaDetailViewModel.Factory>(
                    creationCallback = { factory ->
                        factory.create(productId = it.productId)
                    }
                )
                PizzaDetailScreen(
                    innerPadding = innerPadding,
                    onBackClick = onBack,
                    onAddToCartClick = { /* TODO hook cart later */ },
                    viewModel = viewModel,
                )
            }
            entry<CartRoute> {
                CartScreen(innerPadding = innerPadding)
            }
            entry<HistoryRoute> {
                HistoryScreen(innerPadding = innerPadding)
            }
        }
    )
}
