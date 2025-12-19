package com.example.uilogin.cart.screen

import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import com.example.domain.model.MenuItem
import com.example.uilogin.utils.formatting.toFormattedCurrency

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
        title = name,
        subtitleLines = emptyList(),
        imageUrl = imageUrl,
        quantity = quantity,
        unitPriceFormatted = price.toFormattedCurrency(),
        lineTotalFormatted = lineTotal.toFormattedCurrency(),
    )

    is CartItem.Pizza -> {
        val subtitle = toppings
            .filter { it.quantity > 0 }
            .map { "${it.quantity} x ${it.name}" }

        CartLineDisplayModel(
            lineId = lineId,
            title = name,
            subtitleLines = subtitle,
            imageUrl = imageUrl,
            quantity = quantity,
            unitPriceFormatted = basePrice.toFormattedCurrency(),
            lineTotalFormatted = lineTotal.toFormattedCurrency(),
        )
    }
}

fun MenuItem.toRecommendedItemDisplayModel() = RecommendedItemDisplayModel(
    id = id,
    title = name,
    price = price,
    priceFormatted = price.toFormattedCurrency(),
    imageUrl = imageUrl,
    category = (this as MenuItem.OtherMenuItem).category.name,
)

fun RecommendedItemDisplayModel.toCartItemDisplayModel() = CartItem.Other(
    lineId = id,
    productId = id,
    name = title,
    imageUrl = imageUrl,
    price = price,
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
