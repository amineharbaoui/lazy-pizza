package com.example.checkout.order.ui.pastorders

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
import com.example.core.designsystem.R
import com.example.core.designsystem.components.DsButton
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
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Right)),
    ) {
        DsTopBar.Secondary(title = stringResource(R.string.orders))
        when (val state = uiState) {
            OrderUiState.Loading -> { }

            OrderUiState.NotLoggedIn -> NotLoggedInState(onSignInClick = onSignInClick)

            is OrderUiState.Ready -> {
                OrderScreenContent(orders = state.orders)
            }

            is OrderUiState.Error -> ErrorState(state.message)
        }
    }
}

@Composable
private fun OrderScreenContent(
    orders: List<OrderUi>,
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
                        OrderCard(orderUi = orderUi)
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
                        OrderCard(orderUi = orderUi)
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

@PreviewPhoneTablet
@Composable
private fun OrderScreenContentPreview() {
    val orders = listOf(
        OrderUi(
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
    LazyPizzaThemePreview {
        OrderScreenContent(
            orders = orders,
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
        ErrorState("An error occurred while loading orders.")
    }
}

@PreviewPhoneTablet
@Composable
private fun NotLoggedInStatePreview() {
    LazyPizzaThemePreview {
        NotLoggedInState(onSignInClick = {})
    }
}
