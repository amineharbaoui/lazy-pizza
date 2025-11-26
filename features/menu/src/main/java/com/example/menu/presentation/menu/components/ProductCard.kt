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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil3.compose.rememberAsyncImagePainter
import com.example.designsystem.components.DsCardRow
import com.example.menu.presentation.model.MenuItemDisplayModel

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
            var quantity by rememberSaveable { mutableIntStateOf(0) }

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
                        unitPrice = product.price,
                        image = rememberAsyncImagePainter(product.imageUrl),
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        onRemove = { shouldAddToCart = false },
                    )
                }
            }
        }
    }
}