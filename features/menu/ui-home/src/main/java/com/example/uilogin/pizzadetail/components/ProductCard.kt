package com.example.uilogin.pizzadetail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.rememberAsyncImagePainter
import com.example.designsystem.components.card.DsCardRow
import com.example.uilogin.pizzadetail.MenuItemDisplayModel

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: MenuItemDisplayModel,
    onPizzaClick: (product: MenuItemDisplayModel.Pizza) -> Unit,
    onOtherItemAddClick: (product: MenuItemDisplayModel.Other) -> Unit,
    onOtherItemQuantityChange: (product: MenuItemDisplayModel.Other, newQuantity: Int) -> Unit,
) {
    when (product) {
        is MenuItemDisplayModel.Pizza -> {
            DsCardRow.MenuItem(
                modifier = modifier,
                title = product.name,
                description = product.description,
                price = product.formattedUnitPrice,
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
                        price = product.formattedUnitPrice,
                        image = rememberAsyncImagePainter(product.imageUrl),
                        onAddToCart = { onOtherItemAddClick(product) },
                    )
                } else {
                    DsCardRow.CartItem(
                        modifier = modifier,
                        title = product.name,
                        quantity = product.quantity,
                        unitPriceText = product.formattedUnitPrice,
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
