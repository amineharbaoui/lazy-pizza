package com.example.uilogin.cart.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.designsystem.R
import com.example.designsystem.components.DsButton
import com.example.designsystem.components.DsTopBar
import com.example.designsystem.components.card.DsCardRow
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.designsystem.utils.isWideLayout
import com.example.uilogin.cart.screen.components.RecommendationsSection

@Composable
fun CartScreen(
    innerPadding: PaddingValues,
    onNavigateToCheckout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        DsTopBar.Secondary(title = stringResource(R.string.cart))
        when (val state = uiState) {
            CartUiState.Empty -> EmptyState()
            CartUiState.Loading -> LoadingState()
            is CartUiState.Success -> CartScreenContent(
                cartUiState = state,
                onNavigateToCheckout = onNavigateToCheckout,
                onAddToCart = viewModel::onAddToCart,
                onLineQuantityChange = viewModel::onLineQuantityChange,
                onRemoveLine = viewModel::onRemoveLine,
            )
        }
    }
}

@Composable
fun CartScreenContent(
    cartUiState: CartUiState.Success,
    onNavigateToCheckout: () -> Unit,
    onAddToCart: (item: RecommendedItemDisplayModel) -> Unit,
    onLineQuantityChange: (item: CartLineDisplayModel, newQuantity: Int) -> Unit,
    onRemoveLine: (lineId: String) -> Unit,
) {
    if (isWideLayout()) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CartItemsSection(
                    items = cartUiState.cart.items,
                    onLineQuantityChange = onLineQuantityChange,
                    onRemove = onRemoveLine,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = AppColors.SurfaceHigher, shape = RoundedCornerShape(12.dp)),
            ) {
                Spacer(Modifier.height(8.dp))
                RecommendationsSection(
                    modifier = Modifier.padding(vertical = 12.dp),
                    recommendedItems = cartUiState.recommendedItems,
                    onAddToCart = onAddToCart,
                )
                DsButton.Filled(
                    text = cartUiState.cart.totalPriceFormatted,
                    onClick = {},
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                )
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                CartItemsSection(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    items = cartUiState.cart.items,
                    onLineQuantityChange = onLineQuantityChange,
                    onRemove = onRemoveLine,
                )
                Spacer(Modifier.height(12.dp))
                RecommendationsSection(
                    recommendedItems = cartUiState.recommendedItems,
                    onAddToCart = onAddToCart,
                )
            }
            DsButton.Filled(
                text = "Proceed to Checkout  ${cartUiState.cart.totalPriceFormatted}",
                onClick = onNavigateToCheckout,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Loading…")
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Your Cart Is Empty",
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
        )
        Text(
            text = "Head back to the menu and grab a pizza you love.",
            style = AppTypography.Body3Regular,
            color = AppColors.TextPrimary,
        )
    }
}

@Composable
private fun CartItemsSection(
    items: List<CartLineDisplayModel>,
    onLineQuantityChange: (item: CartLineDisplayModel, newQuantity: Int) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth(),
    ) {
        items.forEach { item ->
            DsCardRow.CartItem(
                title = item.title,
                subtitleLines = item.subtitleLines,
                unitPriceText = item.unitPriceFormatted,
                totalPriceText = item.lineTotalFormatted,
                image = rememberAsyncImagePainter(item.imageUrl),
                quantity = item.quantity,
                onQuantityChange = { newQty -> onLineQuantityChange(item, newQty) },
                onRemove = { onRemove(item.lineId) },
            )
        }
    }
}

@PreviewPhoneTablet
@Preview
@Composable
private fun CartScreenPreview() {
    LazyPizzaThemePreview {
        CartScreenContent(
            cartUiState = CartUiState.Success(
                cart = CartDisplayModel(
                    items = listOf(
                        CartLineDisplayModel(
                            lineId = "1",
                            title = "Margherita",
                            imageUrl = "",
                            unitPriceFormatted = "$8.99",
                            quantity = 2,
                            lineTotalFormatted = "$17.98",
                            subtitleLines = emptyList(),
                        ),
                        CartLineDisplayModel(
                            lineId = "2",
                            title = "Pepperoni",
                            imageUrl = "",
                            unitPriceFormatted = "$12.99",
                            quantity = 1,
                            lineTotalFormatted = "$12.99",
                            subtitleLines = emptyList(),
                        ),
                    ),
                    totalPriceFormatted = "$12.99",
                ),
                recommendedItems = listOf(
                    RecommendedItemDisplayModel(
                        id = "",
                        title = "Chocolate Ice Cream",
                        price = 2.44,
                        priceFormatted = "$2.44",
                        imageUrl = "",
                    ),
                    RecommendedItemDisplayModel(
                        id = "",
                        title = "Ice Cream",
                        price = 2.44,
                        priceFormatted = "$2.44",
                        imageUrl = "",
                    ),
                    RecommendedItemDisplayModel(
                        id = "",
                        title = "Chocolate Ice Cream",
                        price = 2.44,
                        priceFormatted = "$2.44",
                        imageUrl = "",
                    ),
                ),
            ),
            onLineQuantityChange = { _, _ -> },
            onRemoveLine = {},
            onAddToCart = {},
            onNavigateToCheckout = {},
        )
    }
}
