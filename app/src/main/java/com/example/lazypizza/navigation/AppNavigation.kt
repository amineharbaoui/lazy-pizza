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
import com.example.history.presentation.HistoryRoute
import com.example.history.presentation.HistoryScreen
import com.example.uilogin.AuthRoute
import com.example.uilogin.PhoneAuthScreen
import com.example.uilogin.cart.screen.CartRoute
import com.example.uilogin.cart.screen.CartScreen
import com.example.uilogin.pizzadetail.MenuRoute
import com.example.uilogin.pizzadetail.MenuScreen
import com.example.uilogin.pizzadetail.PizzaDetailRoute
import com.example.uilogin.pizzadetail.PizzaDetailScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RootNavGraph(
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
                CartScreen(innerPadding = innerPadding)
            }
            entry<HistoryRoute> {
                HistoryScreen(innerPadding = innerPadding)
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
        },
    )
}
