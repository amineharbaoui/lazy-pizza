package com.example.lazypizza.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.example.auth.ui.login.PhoneAuthScreen
import com.example.cart.ui.cart.CartScreen
import com.example.checkout.order.ui.pastorders.OrdersScreen
import com.example.menu.ui.home.MenuScreen
import com.example.menu.ui.pizzadetail.PizzaDetailArgs
import com.example.menu.ui.pizzadetail.PizzaDetailScreen
import com.example.order.ui.checkout.CheckoutScreen

@Composable
fun AppNavigation(
    backStack: SnapshotStateList<NavKey>,
    onBack: () -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val currentRoute = backStack.last()
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }
    SharedTransitionLayout(modifier = modifier) {
        AnimatedContent(
            targetState = currentRoute,
            label = "navTransition",
            transitionSpec = {
                fadeIn(tween(220)) togetherWith fadeOut(tween(220))
            },
        ) { route ->
            when (route) {
                MenuRoute -> {
                    MenuScreen(
                        innerPadding = innerPadding,
                        onProductClick = { productId ->
                            backStack.add(PizzaDetailRoute.Create(productId))
                        },
                        listState = listState,
                        gridState = gridState,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onNavigateToAuth = { backStack.add(AuthRoute) },
                    )
                }

                is PizzaDetailRoute -> {
                    PizzaDetailScreen(
                        args = when (route) {
                            is PizzaDetailRoute.Create -> PizzaDetailArgs.Create(route.productId)
                            is PizzaDetailRoute.Edit -> PizzaDetailArgs.Edit(route.productId, route.lineId)
                        },
                        innerPadding = innerPadding,
                        onBackClick = onBack,
                        onNavigateToMenu = { onBack() },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                    )
                }

                CartRoute -> {
                    CartScreen(
                        innerPadding = innerPadding,
                        onNavigateToCheckout = {
                            backStack.add(CheckoutRoute)
                        },
                        onNavigateToMenu = {
                            backStack.add(MenuRoute)
                        },
                        onNavigateToDetails = { productId, lineId ->
                            backStack.add(PizzaDetailRoute.Edit(productId, lineId))
                        },
                    )
                }

                OrdersRoute -> {
                    OrdersScreen(
                        innerPadding = innerPadding,
                        onSignInClick = {
                            backStack.add(AuthRoute)
                        },
                    )
                }

                AuthRoute -> {
                    PhoneAuthScreen(
                        innerPadding = innerPadding,
                        onAuthSuccess = {
                            onBack()
                        },
                        onNavigateToMenuScreen = { backStack.add(MenuRoute) },
                    )
                }

                CheckoutRoute -> {
                    CheckoutScreen(
                        innerPadding = innerPadding,
                        onBackClick = { backStack.removeAt(backStack.lastIndex) },
                        onNavigateToAuth = { backStack.add(AuthRoute) },
                        onOrderPlace = { },
                        onBackToMenuClick = { backStack.add(MenuRoute) },
                        onOrderItemClick = { productId ->
                        },
                    )
                }
            }
        }
    }
}
