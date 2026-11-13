package com.example.lazypizza.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.example.auth.ui.login.PhoneAuthScreen
import com.example.cart.ui.cart.CartScreen
import com.example.menu.ui.home.MenuScreen
import com.example.menu.ui.pizzadetail.PizzaDetailArgs
import com.example.menu.ui.pizzadetail.PizzaDetailScreen
import com.example.order.ui.checkout.CheckoutScreen
import com.example.order.ui.pastorders.OrdersScreen

@Composable
fun AppNavigation(
    backStack: SnapshotStateList<NavKey>,
    onBack: () -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    // Hoisted outside NavDisplay: a tab switch pops the Menu entry and its saved state,
    // and the menu keeps its scroll position across tab switches.
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState()
    }
    SharedTransitionLayout(modifier = modifier) {
        NavDisplay(
            backStack = backStack,
            onBack = onBack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            transitionSpec = { fade() },
            popTransitionSpec = { fade() },
            predictivePopTransitionSpec = { fade() },
            entryProvider = entryProvider {
                entry<MenuRoute> {
                    MenuScreen(
                        innerPadding = innerPadding,
                        onProductClick = { productId ->
                            backStack.add(PizzaDetailRoute.Create(productId))
                        },
                        listState = listState,
                        gridState = gridState,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        onNavigateToAuth = { backStack.add(AuthRoute) },
                    )
                }

                entry<PizzaDetailRoute.Create> { route ->
                    PizzaDetailEntry(
                        args = PizzaDetailArgs.Create(route.productId),
                        innerPadding = innerPadding,
                        onBack = onBack,
                    )
                }

                entry<PizzaDetailRoute.Edit> { route ->
                    PizzaDetailEntry(
                        args = PizzaDetailArgs.Edit(route.productId, route.lineId),
                        innerPadding = innerPadding,
                        onBack = onBack,
                    )
                }

                entry<CartRoute> {
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

                entry<OrdersRoute> {
                    OrdersScreen(
                        innerPadding = innerPadding,
                        onSignInClick = {
                            backStack.add(AuthRoute)
                        },
                        onNavigateToCart = {
                            // The same as tapping the Cart tab.
                            backStack.clear()
                            backStack.add(CartRoute)
                        },
                    )
                }

                entry<AuthRoute> {
                    PhoneAuthScreen(
                        innerPadding = innerPadding,
                        onAuthSuccess = {
                            onBack()
                        },
                        onNavigateToMenuScreen = { backStack.add(MenuRoute) },
                    )
                }

                entry<CheckoutRoute> {
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
            },
        )
    }
}

@Composable
private fun SharedTransitionScope.PizzaDetailEntry(
    args: PizzaDetailArgs,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
) {
    PizzaDetailScreen(
        args = args,
        innerPadding = innerPadding,
        onBackClick = onBack,
        onNavigateToMenu = { onBack() },
        sharedTransitionScope = this,
        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
    )
}

private fun fade(): ContentTransform = fadeIn(tween(220)) togetherWith fadeOut(tween(220))
