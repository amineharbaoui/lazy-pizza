package com.example.ui.cart.screen

import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import com.example.domain.model.MenuItem
import com.example.domain.model.OtherCartItem
import com.example.domain.model.PizzaCartItem
import com.example.ui.utils.formatting.toFormattedCurrency

fun Cart.toDisplayModel(): CartDisplayModel {
    val lines = items.map { it.toDisplayModel() }
    return CartDisplayModel(
        items = lines,
        totalPriceFormatted = subtotal.toFormattedCurrency(),
    )
}

private fun CartItem.toDisplayModel(): CartLineDisplayModel = when (this) {
    is OtherCartItem -> CartLineDisplayModel(
        lineId = lineId,
        title = name,
        subtitleLines = emptyList(),
        imageUrl = imageUrl,
        quantity = quantity,
        unitPriceFormatted = price.toFormattedCurrency(),
        lineTotalFormatted = lineTotal.toFormattedCurrency(),
    )

    is PizzaCartItem -> {
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
)

fun RecommendedItemDisplayModel.toCartItemDisplayModel() = OtherCartItem(
    lineId = id,
    productId = id,
    name = title,
    imageUrl = imageUrl,
    price = price,
    quantity = 1,
)
