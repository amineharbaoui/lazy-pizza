package com.example.menu.presentation.menu.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil3.compose.rememberAsyncImagePainter
import com.example.designsystem.components.DsCardRow
import com.example.menu.presentation.menu.MenuItemDisplayModel
import com.example.ui.utils.formatting.toFormattedCurrency

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: MenuItemDisplayModel,
    onProductClick: (productId: String) -> Unit,
) {
    when (product) {
        is MenuItemDisplayModel.Pizza -> {
            DsCardRow.MenuItem(
                modifier = modifier,
                title = product.name,
                description = product.description,
                price = product.formattedPrice,
                image = rememberAsyncImagePainter(product.imageUrl),
                onClick = { onProductClick(product.id) },
            )
        }

        else -> {
            var shouldAddToCart by rememberSaveable(product.id) { mutableStateOf(false) }
            var quantity by rememberSaveable(product.id) { mutableIntStateOf(0) }
            val totalPriceText by remember(product.price, quantity) {
                mutableStateOf((product.price * quantity).toFormattedCurrency())
            }

            AnimatedContent(
                targetState = shouldAddToCart,
                label = "cardSwitch",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                }
            ) { targetState ->
                if (!targetState) {
                    DsCardRow.AddToCartItem(
                        modifier = modifier,
                        title = product.name,
                        price = product.formattedPrice,
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onAddToCart = {
                            shouldAddToCart = true
                            quantity = 1
                        },
                    )
                } else {
                    DsCardRow.CartItem(
                        modifier = modifier,
                        title = product.name,
                        quantity = quantity,
                        unitPriceText = product.formattedPrice,
                        totalPriceText = totalPriceText,
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onQuantityChange = { newQty ->
                            quantity = newQty
                        },
                        onRemove = {
                            shouldAddToCart = false
                            quantity = 0
                        },
                    )
                }
            }
        }
    }
}