package com.example.cart.screen

import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import com.example.domain.model.MenuItem
import com.example.menu.utils.formatting.toFormattedCurrency

fun Cart.toDisplayModel(): CartDisplayModel {
    val lines = items.map { it.toDisplayModel() }
    return CartDisplayModel(
        items = lines,
        totalPriceFormatted = subtotal.toFormattedCurrency(),
    )
}

private fun CartItem.toDisplayModel(): CartLineDisplayModel = when (this) {
    is CartItem.Other -> CartLineDisplayModel(
        lineId = lineId,
        name = name,
        subtitleLines = emptyList(),
        imageUrl = imageUrl,
        quantity = quantity,
        unitPriceFormatted = unitPrice.toFormattedCurrency(),
        lineTotalFormatted = lineTotal.toFormattedCurrency(),
    )

    is CartItem.Pizza -> {
        val subtitle = toppings
            .filter { it.quantity > 0 }
            .map { "${it.quantity} x ${it.name}" }

        CartLineDisplayModel(
            lineId = lineId,
            name = name,
            subtitleLines = subtitle,
            imageUrl = imageUrl,
            quantity = quantity,
            unitPriceFormatted = unitPrice.toFormattedCurrency(),
            lineTotalFormatted = lineTotal.toFormattedCurrency(),
        )
    }
}

fun MenuItem.toRecommendedItemDisplayModel() = RecommendedItemDisplayModel(
    id = id,
    name = name,
    unitPrice = unitPrice,
    unitPriceFormatted = unitPrice.toFormattedCurrency(),
    imageUrl = imageUrl,
    category = category,
)

fun RecommendedItemDisplayModel.toCartItemDisplayModel() = CartItem.Other(
    lineId = id,
    productId = id,
    name = name,
    imageUrl = imageUrl,
    unitPrice = unitPrice,
    category = category,
    quantity = 1,
)

fun mapToCartUiState(
    cart: Cart,
    recommendedItems: List<MenuItem>,
): CartUiState = if (cart.items.isEmpty()) {
    CartUiState.Empty
} else {
    CartUiState.Success(
        cart = cart.toDisplayModel(),
        recommendedItems = recommendedItems.map { it.toRecommendedItemDisplayModel() },
    )
}
