package com.example.lazypizza.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.cart.screen.CartRoute
import com.example.cart.screen.CartScreen
import com.example.checkout.ui.past.orders.OrdersRoute
import com.example.checkout.ui.past.orders.OrdersScreen
import com.example.menu.AuthRoute
import com.example.menu.PhoneAuthScreen
import com.example.menu.detail.PizzaDetailRoute
import com.example.menu.detail.PizzaDetailScreen
import com.example.menu.home.MenuRoute
import com.example.menu.home.MenuScreen
import com.example.ui.checkout.CheckoutRoute
import com.example.ui.checkout.CheckoutScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    backStack: SnapshotStateList<NavKey>,
    onBack: () -> Unit,
    innerPadding: PaddingValues,
) {
    var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }
    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<MenuRoute> {
                MenuScreen(
                    innerPadding = innerPadding,
                    onProductClick = { productId ->
                        backStack.add(PizzaDetailRoute(productId = productId))
                    },
                    listState = listState,
                    gridState = gridState,
                    isLoggedIn = isLoggedIn,
                    onNavigateToAuth = {
                        backStack.add(AuthRoute)
                    },
                    onLogout = { isLoggedIn = false },
                )
            }
            entry<PizzaDetailRoute> {
                PizzaDetailScreen(
                    innerPadding = innerPadding,
                    onBackClick = onBack,
                    onNavigateToMenu = { backStack.removeAt(backStack.lastIndex) },
                    productId = requireNotNull(it.productId),
                )
            }
            entry<CartRoute> {
                CartScreen(
                    innerPadding = innerPadding,
                    onNavigateToCheckout = {
                        backStack.add(CheckoutRoute)
                    },
                )
            }
            entry<AuthRoute> {
                PhoneAuthScreen(
                    innerPadding = innerPadding,
                    onAuthSuccess = {
                        isLoggedIn = true
                        onBack()
                    },
                    onNavigateToMenuScreen = { backStack.add(MenuRoute) },
                )
            }
            entry<CheckoutRoute> {
                CheckoutScreen(
                    onBackClick = { backStack.removeAt(backStack.lastIndex) },
                    onNavigateToAuth = { backStack.add(AuthRoute) },
                    onOrderPlace = { },
                    onBackToMenuClick = { backStack.add(MenuRoute) },
                    onOrderItemClick = { productId ->
//                        productId?.let {
//                            backStack.add(PizzaDetailRoute(productId = productId))
//                        } ?: run {
//                            backStack.add(MenuRoute)
//                        }
                        // FixMe : how
                    },
                )
            }
            entry<OrdersRoute> {
                OrdersScreen(innerPadding = innerPadding)
            }
        },
    )
}
