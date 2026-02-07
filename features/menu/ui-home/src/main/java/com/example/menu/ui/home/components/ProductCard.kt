package com.example.menu.ui.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.rememberAsyncImagePainter
import com.example.core.designsystem.components.card.DsCardRow
import com.example.menu.ui.home.MenuItemDisplayModel

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: MenuItemDisplayModel,
    onPizzaClick: (product: MenuItemDisplayModel.Pizza) -> Unit,
    onOtherItemAddClick: (product: MenuItemDisplayModel.Other) -> Unit,
    onOtherItemQuantityChange: (product: MenuItemDisplayModel.Other, newQuantity: Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    when (product) {
        is MenuItemDisplayModel.Pizza -> {
            val sharedKey = "pizza-image-${product.id}"
            DsCardRow.MenuItem(
                modifier = modifier,
                imageModifier = with(sharedTransitionScope) {
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(sharedKey),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                },
                title = product.name,
                description = product.description,
                price = product.unitPriceFormatted,
                image = rememberAsyncImagePainter(product.imageUrl),
                onClick = { onPizzaClick(product) },

            )
        }

        is MenuItemDisplayModel.Other -> {
            AnimatedContent(
                targetState = product.inCart,
                label = "cardSwitch",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                },
            ) { targetState ->
                if (!targetState) {
                    DsCardRow.AddToCartItem(
                        modifier = modifier,
                        title = product.name,
                        price = product.unitPriceFormatted,
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onAddToCart = { onOtherItemAddClick(product) },
                    )
                } else {
                    DsCardRow.CartItem(
                        modifier = modifier,
                        title = product.name,
                        quantity = product.quantity,
                        unitPriceText = product.unitPriceFormatted,
                        totalPriceText = product.totalPriceFormatted,
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onQuantityChange = { newQty ->
                            onOtherItemQuantityChange(product, newQty)
                        },
                        onRemove = {
                            onOtherItemQuantityChange(product, 0)
                        },
                    )
                }
            }
        }
    }
}
