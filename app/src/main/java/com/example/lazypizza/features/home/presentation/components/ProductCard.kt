package com.example.lazypizza.features.home.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import coil3.compose.rememberAsyncImagePainter
import com.example.core.designsystem.components.DsCardRow
import com.example.lazypizza.features.home.domain.models.Product
import com.example.lazypizza.features.home.domain.models.ProductCategory.PIZZA
import java.text.NumberFormat

@Composable
fun ProductCard(
    product: Product,
    onProductClick: (productId: String) -> Unit,
) {
    when (product.category) {
        PIZZA -> {
            DsCardRow.MenuItem(
                title = product.name,
                description = product.description,
                price = product.price.toString(),
                image = rememberAsyncImagePainter(product.imageUrl),
                onClick = { onProductClick(product.id) },
            )
        }

        else -> {
            var qty by rememberSaveable(product.id) { mutableIntStateOf(0) }

            AnimatedContent(
                targetState = qty > 0,
                label = "cardSwitch",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                }
            ) { inCart ->
                if (!inCart) {
                    DsCardRow.AddToCartItem(
                        title = product.name,
                        price = product.price.asMoney(),
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onAddToCart = { qty = 1 },
                        onClick = { onProductClick(product.id) }
                    )
                } else {
                    DsCardRow.CartItem(
                        title = product.name,
                        unitPrice = product.price,
                        quantity = qty,
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onIncrease = { qty += 1 },
                        onDecrease = {
                            if (qty <= 1) qty = 0 else qty -= 1
                        },
                        onRemove = { qty = 0 },
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

private fun Double.asMoney(): String = NumberFormat.getCurrencyInstance().format(this)