package com.example.checkout.order.ui.pastorders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.windowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.R
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsDialog
import com.example.core.designsystem.components.DsTopBar
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.designsystem.utils.isWideLayout
import com.example.core.model.OrderStatus

@Composable
fun OrdersScreen(
    innerPadding: PaddingValues,
    onSignInClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val menuUnavailableMessage = stringResource(com.example.order.checkout.ui.pastorders.R.string.order_again_error_menu_unavailable)
    val cartUpdateFailedMessage = stringResource(com.example.order.checkout.ui.pastorders.R.string.order_again_error_cart_update_failed)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                OrdersUiEvent.NavigateToCart -> onNavigateToCart()
                is OrdersUiEvent.ShowOrderAgainError -> snackbarHostState.showSnackbar(
                    when (event.error) {
                        OrderAgainErrorUi.MENU_UNAVAILABLE -> menuUnavailableMessage
                        OrderAgainErrorUi.CART_UPDATE_FAILED -> cartUpdateFailedMessage
                    },
                )
            }
        }
    }

    OrdersScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onSignInClick = onSignInClick,
        onOrderAgainClick = viewModel::orderAgain,
        onGoToCartClick = viewModel::goToCartFromSkippedItems,
        onDismissOrderAgainDialog = viewModel::dismissOrderAgainDialog,
        modifier = modifier.padding(bottom = innerPadding.calculateBottomPadding()),
    )
}

@Composable
private fun OrdersScreenContent(
    uiState: OrderUiState,
    snackbarHostState: SnackbarHostState,
    onSignInClick: () -> Unit,
    onOrderAgainClick: (orderNumber: String) -> Unit,
    onGoToCartClick: () -> Unit,
    onDismissOrderAgainDialog: () -> Unit,
    modifier: Modifier = Modifier,
    isDialogInline: Boolean = false,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Right)),
        ) {
            DsTopBar.Secondary(title = stringResource(R.string.orders))
            when (uiState) {
                OrderUiState.Loading -> { }

                OrderUiState.NotLoggedIn -> NotLoggedInState(onSignInClick = onSignInClick)

                is OrderUiState.Ready -> OrderList(
                    orders = uiState.orders,
                    isOrderAgainEnabled = uiState.orderAgain == OrderAgainUiState.Idle,
                    onOrderAgainClick = onOrderAgainClick,
                )

                is OrderUiState.Error -> ErrorState(uiState.message)
            }
        }

        (uiState as? OrderUiState.Ready)?.orderAgain?.let { orderAgain ->
            OrderAgainDialog(
                orderAgain = orderAgain,
                isInline = isDialogInline,
                onGoToCartClick = onGoToCartClick,
                onDismiss = onDismissOrderAgainDialog,
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun BoxScope.OrderAgainDialog(
    orderAgain: OrderAgainUiState,
    isInline: Boolean,
    onGoToCartClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val (title, skippedItems, hasAddedItems) = when (orderAgain) {
        is OrderAgainUiState.SomeSkipped -> Triple(
            com.example.order.checkout.ui.pastorders.R.string.order_again_some_skipped_title,
            orderAgain.skippedItems,
            true,
        )
        is OrderAgainUiState.NothingAvailable -> Triple(
            com.example.order.checkout.ui.pastorders.R.string.order_again_nothing_available_title,
            orderAgain.skippedItems,
            false,
        )
        OrderAgainUiState.Idle, OrderAgainUiState.InProgress -> return
    }
    val titleText = stringResource(title)
    val message = skippedItems.map { skippedItemLabel(it) }.joinToString(separator = "\n")
    val closeText = stringResource(com.example.order.checkout.ui.pastorders.R.string.order_again_close)
    val primaryText = if (hasAddedItems) {
        stringResource(
            com.example.order.checkout.ui.pastorders.R.string.order_again_go_to_cart,
        )
    } else {
        closeText
    }
    val onPrimaryClick = if (hasAddedItems) onGoToCartClick else onDismiss

    if (isInline) {
        // Dialog windows are invisible to screenshot capture, so previews draw the surface in place.
        Box(
            Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = DIALOG_SCRIM_ALPHA)),
        )
        DsDialog.ConfirmContent(
            title = titleText,
            message = message,
            primaryButtonText = primaryText,
            onPrimaryClick = onPrimaryClick,
            onSecondaryClick = onDismiss,
            secondaryButtonText = closeText,
            showSecondaryButton = hasAddedItems,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
        )
    } else {
        DsDialog.Confirm(
            title = titleText,
            message = message,
            primaryButtonText = primaryText,
            onPrimaryClick = onPrimaryClick,
            onDismissRequest = onDismiss,
            secondaryButtonText = closeText,
            showSecondaryButton = hasAddedItems,
        )
    }
}

private const val DIALOG_SCRIM_ALPHA = 0.32f

@Composable
private fun skippedItemLabel(item: SkippedItemUi): String = when (item) {
    is SkippedItemUi.Product -> item.name
    is SkippedItemUi.Topping -> stringResource(
        com.example.order.checkout.ui.pastorders.R.string.order_again_skipped_topping,
        item.toppingName,
        item.pizzaName,
    )
}

@Composable
private fun OrderList(
    orders: List<OrderUi>,
    isOrderAgainEnabled: Boolean,
    onOrderAgainClick: (orderNumber: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEmpty = orders.isEmpty()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        if (isWideLayout()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isEmpty) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = "NoOrders",
                    ) {
                        NoOrders()
                    }
                } else {
                    items(
                        items = orders,
                        key = { it.orderNumberLabel },
                        contentType = { "OrderCard" },
                    ) { orderUi ->
                        OrderCard(
                            orderUi = orderUi,
                            isOrderAgainEnabled = isOrderAgainEnabled,
                            onOrderAgainClick = { onOrderAgainClick(orderUi.orderNumber) },
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isEmpty) {
                    item(contentType = "NoOrders") {
                        NoOrders()
                    }
                } else {
                    items(
                        items = orders,
                        key = { it.orderNumberLabel },
                        contentType = { "OrderCard" },
                    ) { orderUi ->
                        OrderCard(
                            orderUi = orderUi,
                            isOrderAgainEnabled = isOrderAgainEnabled,
                            onOrderAgainClick = { onOrderAgainClick(orderUi.orderNumber) },
                        )
                    }
                }
                item(contentType = "BottomSpacer") { Spacer(Modifier.height(24.dp)) }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun NotLoggedInState(
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.not_logged_in),
            contentDescription = stringResource(com.example.order.checkout.ui.pastorders.R.string.not_signed_in_image_description),
            modifier = Modifier.size(128.dp),
        )
        Text(
            text = stringResource(com.example.order.checkout.ui.pastorders.R.string.not_signed_in_title),
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
        )
        Text(
            text = stringResource(com.example.order.checkout.ui.pastorders.R.string.not_signed_in_message),
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        DsButton.Filled(
            text = stringResource(com.example.order.checkout.ui.pastorders.R.string.sign_in),
            onClick = onSignInClick,
        )
    }
}

@Composable
private fun NoOrders(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(128.dp),
            painter = painterResource(R.drawable.no_orders_yet),
            contentDescription = "No Orders Yet",
        )
        Text(
            text = "No Orders Yet",
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
        )
        Text(
            text = "Your orders will appear here after your first purchase.",
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        DsButton.Filled(
            text = "Start Shopping",
            onClick = {},
        )
    }
}

@Composable
private fun ErrorState(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.error),
            contentDescription = null,
            modifier = Modifier.size(128.dp),
        )
        Text(
            text = errorMessage,
            style = AppTypography.Body1Medium,
            color = AppColors.TextPrimary,
        )
    }
}

private val previewOrders = listOf(
    OrderUi(
        orderNumber = "123",
        orderNumberLabel = "Order #123",
        dateTimeLabel = "September 25, 12:15",
        items = listOf(
            OrderItem(
                name = "1 x Margherita",
                toppings = listOf(),
            ),
            OrderItem(
                name = "1 x Margherita",
                toppings = listOf(
                    "1 x Extra cheese",
                    "2 x Pepperoni",
                ),
            ),
        ),
        totalAmountLabel = "$25.45",
        status = OrderStatus.COMPLETED,
    ),
    OrderUi(
        orderNumber = "1234",
        orderNumberLabel = "Order #1234",
        dateTimeLabel = "September 25, 12:15",
        items = listOf(
            OrderItem(
                name = "1 x Margherita",
                toppings = listOf(
                    "1 x Extra cheese",
                    "2 x Pepperoni",
                ),
            ),
            OrderItem(
                name = "1 x Margherita",
                toppings = listOf(
                    "1 x Extra cheese",
                    "2 x Pepperoni",
                ),
            ),
        ),
        totalAmountLabel = "$25.45",
        status = OrderStatus.IN_PROGRESS,
    ),
    OrderUi(
        orderNumber = "12346",
        orderNumberLabel = "Order #12346",
        dateTimeLabel = "September 25, 12:15",
        items = listOf(
            OrderItem(
                name = "1 x Margherita",
                toppings = listOf(
                    "1 x Extra cheese",
                    "2 x Pepperoni",
                ),
            ),
            OrderItem(
                name = "1 x Margherita",
                toppings = listOf(
                    "1 x Extra cheese",
                    "2 x Pepperoni",
                ),
            ),
        ),
        totalAmountLabel = "$25.45",
        status = OrderStatus.CANCELED,
    ),
)

@Composable
private fun OrdersScreenPreview(uiState: OrderUiState) {
    LazyPizzaThemePreview {
        OrdersScreenContent(
            uiState = uiState,
            snackbarHostState = remember { SnackbarHostState() },
            onSignInClick = {},
            onOrderAgainClick = {},
            onGoToCartClick = {},
            onDismissOrderAgainDialog = {},
            isDialogInline = true,
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun EmptyOrderStatePreview() {
    OrdersScreenPreview(OrderUiState.Ready(orders = emptyList()))
}

@PreviewPhoneTablet
@Composable
private fun ErrorStatePreview() {
    OrdersScreenPreview(OrderUiState.Error("An error occurred while loading orders."))
}

// Screenshot previews for the order-again scenarios, captured by OrdersScreenScreenshotTest.

@Preview
@Composable
internal fun OrdersScreenReadyMixedStatusesPreview() {
    OrdersScreenPreview(OrderUiState.Ready(orders = previewOrders))
}

@Preview
@Composable
internal fun OrdersScreenNotLoggedInPreview() {
    OrdersScreenPreview(OrderUiState.NotLoggedIn)
}

@Preview
@Composable
internal fun OrdersScreenSomeSkippedDialogPreview() {
    OrdersScreenPreview(
        OrderUiState.Ready(
            orders = previewOrders,
            orderAgain = OrderAgainUiState.SomeSkipped(
                skippedItems = listOf(
                    SkippedItemUi.Product(name = "Lemonade"),
                    SkippedItemUi.Topping(toppingName = "Truffle", pizzaName = "Margherita"),
                ),
            ),
        ),
    )
}

@Preview
@Composable
internal fun OrdersScreenNothingAvailableDialogPreview() {
    OrdersScreenPreview(
        OrderUiState.Ready(
            orders = previewOrders,
            orderAgain = OrderAgainUiState.NothingAvailable(
                skippedItems = listOf(SkippedItemUi.Product(name = "Lemonade")),
            ),
        ),
    )
}
