package com.example.lazypizza.features.cart.presentation

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.DsCardRow
import com.example.core.designsystem.components.DsTopBar
import com.example.core.designsystem.theme.AppColors
import com.example.core.designsystem.theme.LazyPizzaThemePreview
import com.example.core.designsystem.utils.PreviewPhoneTablet
import com.example.core.designsystem.utils.isWideLayout
import com.example.lazypizza.R
import com.example.lazypizza.features.cart.presentation.components.RecommendationsSection
import java.text.NumberFormat
import java.util.Locale

data class UiCartItem(
    val id: String,
    val title: String,
    val unitPrice: Double,
    var quantity: Int,
    val imageRes: Int,
)

data class UiExtraItem(
    val id: String,
    val title: String,
    val priceText: String,
    val imageRes: Int,
)

@Composable
fun CartScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val isWide = isWideLayout()

    // Local demo state (keeps changes within the screen). In real app, plug repository/ViewModel.
    val items = remember {
        mutableStateListOf(
            UiCartItem("1", "Margherita", 8.99, 2, R.drawable.img_pizza),
            UiCartItem("2", "Pepsi", 1.99, 2, R.drawable.img_pizza),
            UiCartItem("3", "Cookies Ice Cream", 1.49, 1, R.drawable.img_pizza),
        )
    }
    val extras = remember {
        listOf(
            UiExtraItem("e1", "BBQ Sauce", "$0.59", R.drawable.img_bacon),
            UiExtraItem("e2", "Garlic Sauce", "$0.59", R.drawable.img_bacon),
            UiExtraItem("e3", "Vanilla Sauce", "$2.49", R.drawable.img_bacon),
            UiExtraItem("e3", "Vanilla Sauce", "$2.49", R.drawable.img_bacon),
            UiExtraItem("e3", "Vanilla Sauce", "$2.49", R.drawable.img_bacon),
            UiExtraItem("e3", "Vanilla Sauce", "$2.49", R.drawable.img_bacon),
        )
    }

    val currency = NumberFormat.getCurrencyInstance(Locale.US)
    val subtotal = items.sumOf { it.unitPrice * it.quantity }

    Column(
        modifier = modifier
            .background(AppColors.Bg)
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        DsTopBar.Secondary(title = stringResource(R.string.cart))

        if (isWide) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CartItemsSection(
                        items = items,
                        onIncrease = { id ->
                            val index = items.indexOfFirst { it.id == id }
                            if (index >= 0) items[index] = items[index].copy(quantity = items[index].quantity + 1)
                        },
                        onDecrease = { id ->
                            val index = items.indexOfFirst { it.id == id }
                            if (index >= 0) items[index] = items[index].copy(quantity = (items[index].quantity - 1).coerceAtLeast(0))
                        },
                        onRemove = { id -> items.removeAll { it.id == id } }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    RecommendationsSection(
                        extras = extras,
                        totalText = "Proceed to Checkout (${currency.format(subtotal)})",
                        onCheckout = {},
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CartItemsSection(
                    items = items,
                    onIncrease = { id ->
                        val index = items.indexOfFirst { it.id == id }
                        if (index >= 0) items[index] = items[index].copy(quantity = items[index].quantity + 1)
                    },
                    onDecrease = { id ->
                        val index = items.indexOfFirst { it.id == id }
                        if (index >= 0) items[index] = items[index].copy(quantity = (items[index].quantity - 1).coerceAtLeast(0))
                    },
                    onRemove = { id -> items.removeAll { it.id == id } }
                )

                Spacer(Modifier.height(8.dp))

                RecommendationsSection(
                    extras = extras,
                    totalText = "Proceed to Checkout (${currency.format(subtotal)})",
                    onCheckout = {},
                )
            }
        }
    }
}

@Composable
private fun CartItemsSection(
    items: List<UiCartItem>,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
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
                unitPrice = item.unitPrice,
                image = painterResource(id = item.imageRes),
                quantity = item.quantity,
                onQuantityChange = { },
                onRemove = { onRemove(item.id) },
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