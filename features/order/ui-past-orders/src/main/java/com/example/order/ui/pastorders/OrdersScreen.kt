package com.example.order.ui.pastorders

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.windowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsDialog
import com.example.core.designsystem.components.DsTopBar
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.designsystem.utils.isWideLayout
import com.example.core.model.OrderStatus
import com.example.core.designsystem.R as DsR

@Composable
fun OrdersScreen(
    innerPadding: PaddingValues,
    onSignInClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val navigation = (uiState as? OrderUiState.Ready)?.navigation
    LaunchedEffect(navigation) {
        if (navigation == OrdersNavigation.CART) {
            onNavigateToCart()
            viewModel.navigationHandled()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Right)),
    ) {
        DsTopBar.Secondary(title = stringResource(DsR.string.orders))
        OrdersScreenContent(
            state = uiState,
            onSignInClick = onSignInClick,
            onReorderClick = viewModel::reorder,
            onDismissDialog = viewModel::dismissDialog,
        )
    }
}

@Composable
fun OrdersScreenContent(
    state: OrderUiState,
    onSignInClick: () -> Unit,
    onReorderClick: (orderNumber: String) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        OrderUiState.Loading -> { }

        OrderUiState.NotLoggedIn -> NotLoggedInState(onSignInClick = onSignInClick, modifier = modifier)

        is OrderUiState.Ready -> {
            OrderList(
                orders = state.orders,
                isReorderEnabled = !state.isReordering,
                onReorderClick = onReorderClick,
                modifier = modifier,
            )
            state.dialog?.let { dialog ->
                ReorderResultDialog(dialog = dialog, onDismiss = onDismissDialog)
            }
        }

        OrderUiState.Error -> ErrorState(modifier = modifier)
    }
}

@Composable
private fun OrderList(
    orders: List<OrderDisplayModel>,
    isReorderEnabled: Boolean,
    onReorderClick: (orderNumber: String) -> Unit,
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
                        key = { it.orderNumber },
                        contentType = { "OrderCard" },
                    ) { order ->
                        OrderCard(
                            order = order,
                            isReorderEnabled = isReorderEnabled,
                            onReorderClick = { onReorderClick(order.orderNumber) },
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
                        key = { it.orderNumber },
                        contentType = { "OrderCard" },
                    ) { order ->
                        OrderCard(
                            order = order,
                            isReorderEnabled = isReorderEnabled,
                            onReorderClick = { onReorderClick(order.orderNumber) },
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
private fun ReorderResultDialog(
    dialog: ReorderDialog,
    onDismiss: () -> Unit,
) {
    val (title, message) = when (dialog) {
        ReorderDialog.NOTHING_AVAILABLE ->
            R.string.past_orders_nothing_available_title to R.string.past_orders_nothing_available_message
        ReorderDialog.FAILED ->
            R.string.past_orders_reorder_failed_title to R.string.past_orders_reorder_failed_message
    }
    DsDialog.Confirm(
        title = stringResource(title),
        message = stringResource(message),
        primaryButtonText = stringResource(DsR.string.ok),
        onPrimaryClick = onDismiss,
        onDismissRequest = onDismiss,
        showSecondaryButton = false,
    )
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
            painter = painterResource(DsR.drawable.not_logged_in),
            contentDescription = stringResource(R.string.not_signed_in_image_description),
            modifier = Modifier.size(128.dp),
        )
        Text(
            text = stringResource(R.string.not_signed_in_title),
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
        )
        Text(
            text = stringResource(R.string.not_signed_in_message),
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        DsButton.Filled(
            text = stringResource(R.string.sign_in),
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
            painter = painterResource(DsR.drawable.no_orders_yet),
            contentDescription = stringResource(R.string.past_orders_no_orders_image_description),
        )
        Text(
            text = stringResource(R.string.past_orders_no_orders_title),
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
        )
        Text(
            text = stringResource(R.string.past_orders_no_orders_message),
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        DsButton.Filled(
            text = stringResource(R.string.past_orders_start_shopping),
            onClick = {},
        )
    }
}

@Composable
private fun ErrorState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(DsR.drawable.error),
            contentDescription = null,
            modifier = Modifier.size(128.dp),
        )
        Text(
            text = stringResource(R.string.past_orders_orders_load_error),
            style = AppTypography.Body1Medium,
            color = AppColors.TextPrimary,
        )
    }
}

private val previewOrders = listOf(
    OrderDisplayModel(
        orderNumber = "123",
        dateTimeLabel = "September 25, 12:15",
        items = listOf(
            OrderItemDisplayModel(
                quantity = 1,
                name = "Margherita",
                toppings = listOf(),
            ),
            OrderItemDisplayModel(
                quantity = 1,
                name = "Margherita",
                toppings = listOf(
                    OrderToppingDisplayModel(quantity = 1, name = "Extra cheese"),
                    OrderToppingDisplayModel(quantity = 2, name = "Pepperoni"),
                ),
            ),
        ),
        totalAmountLabel = "$25.45",
        status = OrderStatus.COMPLETED,
    ),
    OrderDisplayModel(
        orderNumber = "1234",
        dateTimeLabel = "September 25, 12:15",
        items = listOf(
            OrderItemDisplayModel(
                quantity = 1,
                name = "Margherita",
                toppings = listOf(
                    OrderToppingDisplayModel(quantity = 1, name = "Extra cheese"),
                    OrderToppingDisplayModel(quantity = 2, name = "Pepperoni"),
                ),
            ),
            OrderItemDisplayModel(
                quantity = 1,
                name = "Margherita",
                toppings = listOf(
                    OrderToppingDisplayModel(quantity = 1, name = "Extra cheese"),
                    OrderToppingDisplayModel(quantity = 2, name = "Pepperoni"),
                ),
            ),
        ),
        totalAmountLabel = "$25.45",
        status = OrderStatus.IN_PROGRESS,
    ),
    OrderDisplayModel(
        orderNumber = "12346",
        dateTimeLabel = "September 25, 12:15",
        items = listOf(
            OrderItemDisplayModel(
                quantity = 1,
                name = "Margherita",
                toppings = listOf(
                    OrderToppingDisplayModel(quantity = 1, name = "Extra cheese"),
                    OrderToppingDisplayModel(quantity = 2, name = "Pepperoni"),
                ),
            ),
            OrderItemDisplayModel(
                quantity = 1,
                name = "Margherita",
                toppings = listOf(
                    OrderToppingDisplayModel(quantity = 1, name = "Extra cheese"),
                    OrderToppingDisplayModel(quantity = 2, name = "Pepperoni"),
                ),
            ),
        ),
        totalAmountLabel = "$25.45",
        status = OrderStatus.CANCELED,
    ),
)

@PreviewPhoneTablet
@Composable
private fun OrderScreenContentPreview() {
    LazyPizzaThemePreview {
        OrdersScreenContent(
            state = OrderUiState.Ready(orders = previewOrders),
            onSignInClick = {},
            onReorderClick = {},
            onDismissDialog = {},
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun ReorderingPreview() {
    LazyPizzaThemePreview {
        OrdersScreenContent(
            state = OrderUiState.Ready(orders = previewOrders, isReordering = true),
            onSignInClick = {},
            onReorderClick = {},
            onDismissDialog = {},
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun NothingAvailableDialogPreview() {
    LazyPizzaThemePreview {
        OrdersScreenContent(
            state = OrderUiState.Ready(orders = previewOrders, dialog = ReorderDialog.NOTHING_AVAILABLE),
            onSignInClick = {},
            onReorderClick = {},
            onDismissDialog = {},
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun ReorderFailedDialogPreview() {
    LazyPizzaThemePreview {
        OrdersScreenContent(
            state = OrderUiState.Ready(orders = previewOrders, dialog = ReorderDialog.FAILED),
            onSignInClick = {},
            onReorderClick = {},
            onDismissDialog = {},
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun EmptyOrderStatePreview() {
    LazyPizzaThemePreview {
        NoOrders()
    }
}

@PreviewPhoneTablet
@Composable
private fun ErrorCartPreview() {
    LazyPizzaThemePreview {
        OrdersScreenContent(
            state = OrderUiState.Error,
            onSignInClick = {},
            onReorderClick = {},
            onDismissDialog = {},
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun NotLoggedInStatePreview() {
    LazyPizzaThemePreview {
        OrdersScreenContent(
            state = OrderUiState.NotLoggedIn,
            onSignInClick = {},
            onReorderClick = {},
            onDismissDialog = {},
        )
    }
}
