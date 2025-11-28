package com.example.cart.presentation

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.cart.presentation.components.RecommendationsSection
import com.example.designsystem.R
import com.example.designsystem.components.DsButton
import com.example.designsystem.components.DsCardRow
import com.example.designsystem.components.DsTopBar
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview
import com.example.designsystem.utils.PreviewPhoneTablet
import com.example.designsystem.utils.isWideLayout

@Composable
fun CartScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .background(AppColors.Bg)
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        DsTopBar.Secondary(title = stringResource(R.string.cart))

        when (val state = uiState) {
            CartUiState.Empty -> EmptyState()
            CartUiState.Loading -> LoadingState()
            is CartUiState.Success -> CartScreenContent(
                state = state,
                onLineQuantityChange = viewModel::onLineQuantityChange,
                onRemoveLine = viewModel::onRemoveLine,
            )
        }
    }
}

@Composable
fun CartScreenContent(
    state: CartUiState.Success,
    onLineQuantityChange: (lineId: String, quantity: Int) -> Unit,
    onRemoveLine: (lineId: String) -> Unit,
) {
    if (isWideLayout()) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CartItemsSection(
                    items = state.cart.items,
                    onIncrease = { lineId, currentQty ->
                        onLineQuantityChange(lineId, currentQty + 1)
                    },
                    onDecrease = { lineId, currentQty ->
                        onLineQuantityChange(
                            lineId,
                            (currentQty - 1).coerceAtLeast(0)
                        )
                    },
                    onRemove = onRemoveLine,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                RecommendationsSection(recommendedItems = state.recommendedItems)
                DsButton.Filled(
                    text = state.cart.totalPriceFormatted,
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CartItemsSection(
                items = state.cart.items,
                onIncrease = { lineId, currentQty ->
                    onLineQuantityChange(lineId, currentQty + 1)
                },
                onDecrease = { lineId, currentQty ->
                    onLineQuantityChange(
                        lineId,
                        (currentQty - 1).coerceAtLeast(0)
                    )
                },
                onRemove = onRemoveLine,
            )
            Spacer(Modifier.height(8.dp))
            RecommendationsSection(recommendedItems = state.recommendedItems)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Loading…")
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .background(AppColors.Bg)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Cart Is Empty",
            style = AppTypography.Title1SemiBold,
            color = AppColors.TextPrimary
        )
        Text(
            text = "Head back to the menu and grab a pizza you love.",
            style = AppTypography.Body3Regular,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
private fun CartItemsSection(
    items: List<CartLineDisplayModel>,
    onIncrease: (lineId: String, currentQuantity: Int) -> Unit,
    onDecrease: (lineId: String, currentQuantity: Int) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            DsCardRow.CartItem(
                title = item.title,
                unitPriceText = item.unitPriceFormatted,
                totalPriceText = item.lineTotalFormatted,
                image = rememberAsyncImagePainter(item.imageUrl),
                quantity = item.quantity,
                onQuantityChange = { newQty ->
                    if (newQty > item.quantity) {
                        onIncrease(item.lineId, item.quantity)
                    } else {
                        onDecrease(item.lineId, item.quantity)
                    }
                },
                onRemove = { onRemove(item.lineId) },
            )
        }
    }
}


@PreviewPhoneTablet
@Composable
private fun CartScreenPreview() {
    LazyPizzaThemePreview {
        CartScreen(
            innerPadding = PaddingValues(0.dp),
        )
    }
}