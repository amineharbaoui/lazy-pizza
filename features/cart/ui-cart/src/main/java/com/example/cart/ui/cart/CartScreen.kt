package com.example.cart.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.cart.ui.cart.components.RecommendationsSection
import com.example.core.designsystem.components.DsButton
import com.example.core.designsystem.components.DsTopBar
import com.example.core.designsystem.components.card.DsCardRow
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.AppTypography
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.designsystem.utils.isWideLayout
import com.example.core.model.ProductCategory
import com.example.core.designsystem.R as DS_R

@Composable
fun CartScreen(
    innerPadding: PaddingValues,
    onNavigateToCheckout: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToDetails: (productId: String, lineId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(bottom = innerPadding.calculateBottomPadding())
            .fillMaxSize(),
    ) {
        DsTopBar.Secondary(title = stringResource(DS_R.string.cart))
        when (val state = uiState) {
            CartUiState.Loading -> LoadingState()
            CartUiState.Empty -> EmptyState(onNavigateToMenu = onNavigateToMenu)
            is CartUiState.Error -> ErrorState(state.message)
            is CartUiState.Success -> CartScreenContent(
                cartUiState = state,
                onNavigateToCheckout = onNavigateToCheckout,
                onNavigateToDetails = onNavigateToDetails,
                onAddToCart = viewModel::addItemToCart,
                onLineQuantityChange = viewModel::updateLineQuantity,
                onRemoveLine = viewModel::removeLine,
            )
        }
    }
}

@Composable
fun CartScreenContent(
    cartUiState: CartUiState.Success,
    onNavigateToCheckout: () -> Unit,
    onNavigateToDetails: (productId: String, lineId: String) -> Unit,
    onAddToCart: (item: RecommendedItemDisplayModel) -> Unit,
    onLineQuantityChange: (item: CartLineDisplayModel, newQuantity: Int) -> Unit,
    onRemoveLine: (lineId: String) -> Unit,
) {
    if (isWideLayout()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CartItemsSection(
                    items = cartUiState.cart.items,
                    onNavigateToDetails = onNavigateToDetails,
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
                    onClick = onNavigateToCheckout,
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
                    onNavigateToDetails = onNavigateToDetails,
                    onLineQuantityChange = onLineQuantityChange,
                    onRemove = onRemoveLine,
                )
                Spacer(Modifier.height(12.dp))
                RecommendationsSection(
                    recommendedItems = cartUiState.recommendedItems,
                    onAddToCart = onAddToCart,
                )
                Spacer(Modifier.height(8.dp))
            }
            DsButton.Filled(
                text = stringResource(
                    R.string.proceed_to_checkout,
                    cartUiState.cart.totalPriceFormatted,
                ),
                onClick = onNavigateToCheckout,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
            )
            Spacer(Modifier.height(32.dp))
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
private fun EmptyState(
    onNavigateToMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(modifier = Modifier) {
            val isLandscape = maxWidth > maxHeight
            Image(
                painter = painterResource(DS_R.drawable.empty_cart),
                contentDescription = "Empty Cart",
                modifier = Modifier.size(
                    if (isLandscape) 128.dp else 256.dp,
                ),
            )
        }
        Text(
            text = "Your Cart Is Empty",
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary,
        )
        Text(
            text = "Looks like you haven't added any items to your cart yet.",
            style = AppTypography.Body3Regular,
            color = AppColors.TextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        DsButton.Filled(
            text = stringResource(R.string.start_ordering),
            onClick = onNavigateToMenu,
        )
    }
}

@Composable
private fun ErrorState(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(modifier = Modifier) {
            val isLandscape = maxWidth > maxHeight
            Image(
                painter = painterResource(DS_R.drawable.error),
                contentDescription = null,
                modifier = Modifier.size(
                    if (isLandscape) 128.dp else 256.dp,
                ),
            )
        }
        Text(
            text = errorMessage,
            style = AppTypography.Body1Medium,
            color = AppColors.TextPrimary,
        )
    }
}

@Composable
private fun CartItemsSection(
    items: List<CartLineDisplayModel>,
    onNavigateToDetails: (productId: String, lineId: String) -> Unit,
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
            val image = if (LocalInspectionMode.current) {
                painterResource(DS_R.drawable.pizza)
            } else {
                rememberAsyncImagePainter(item.imageUrl)
            }
            DsCardRow.CartItem(
                title = item.name,
                subtitleLines = item.subtitleLines,
                unitPriceText = item.unitPriceFormatted,
                totalPriceText = item.lineTotalFormatted,
                image = image,
                quantity = item.quantity,
                onQuantityChange = { newQty -> onLineQuantityChange(item, newQty) },
                onRemove = { onRemove(item.lineId) },
                onClick = { onNavigateToDetails(item.productId, item.lineId) },
            )
        }
    }
}

@PreviewPhoneTablet
@Composable
private fun CartScreenPreview() {
    LazyPizzaThemePreview {
        CartScreenContent(
            cartUiState = CartUiState.Success(
                cart = CartDisplayModel(
                    items = listOf(
                        CartLineDisplayModel(
                            lineId = "1",
                            productId = "p1",
                            name = "Margherita",
                            imageUrl = "",
                            unitPriceFormatted = "$8.99",
                            quantity = 2,
                            lineTotalFormatted = "$17.98",
                            subtitleLines = emptyList(),
                        ),
                        CartLineDisplayModel(
                            lineId = "2",
                            productId = "p2",
                            name = "Pepperoni",
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
                        name = "Chocolate Ice Cream",
                        unitPrice = 2.44,
                        unitPriceFormatted = "$2.44",
                        imageUrl = "",
                        category = ProductCategory.ICE_CREAM,
                    ),
                    RecommendedItemDisplayModel(
                        id = "",
                        name = "Ice Cream",
                        unitPrice = 2.44,
                        unitPriceFormatted = "$2.44",
                        imageUrl = "",
                        category = ProductCategory.ICE_CREAM,
                    ),
                    RecommendedItemDisplayModel(
                        id = "",
                        name = "Chocolate Ice Cream",
                        unitPrice = 2.44,
                        unitPriceFormatted = "$2.44",
                        imageUrl = "",
                        category = ProductCategory.ICE_CREAM,
                    ),
                ),
            ),
            onLineQuantityChange = { _, _ -> },
            onRemoveLine = {},
            onAddToCart = {},
            onNavigateToCheckout = {},
            onNavigateToDetails = { _, _ -> },
        )
    }
}

@PreviewPhoneTablet
@Composable
private fun EmptyCartPreview() {
    LazyPizzaThemePreview {
        EmptyState(onNavigateToMenu = {})
    }
}

@PreviewPhoneTablet
@Composable
private fun ErrorCartPreview() {
    LazyPizzaThemePreview {
        ErrorState("An error occurred while loading your cart.")
    }
}
