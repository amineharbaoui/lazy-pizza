package com.example.cart.screen.mapper

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.screen.CartDisplayModel
import com.example.cart.screen.CartLineDisplayModel
import com.example.cart.screen.CartUiState
import com.example.cart.screen.RecommendedItemDisplayModel
import com.example.domain.model.MenuItem
import com.example.menu.utils.formatting.CurrencyFormatter
import javax.inject.Inject

class CartDomainToUiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) {

    fun mapToCartUiState(
        cart: Cart,
        recommendedItems: List<MenuItem>,
    ): CartUiState = if (cart.items.isEmpty()) {
        CartUiState.Empty
    } else {
        CartUiState.Success(
            cart = mapToCartDisplayModel(cart),
            recommendedItems = recommendedItems.map(::mapToRecommendedItemDisplayModel),
        )
    }

    private fun mapToCartDisplayModel(cart: Cart) = CartDisplayModel(
        items = cart.items.map(::mapToCartLineDisplayModel),
        totalPriceFormatted = currencyFormatter.format(cart.subtotal),
    )

    private fun mapToCartLineDisplayModel(item: CartItem): CartLineDisplayModel = when (item) {
        is CartItem.Other -> CartLineDisplayModel(
            lineId = item.lineId,
            productId = item.productId,
            name = item.name,
            subtitleLines = emptyList(),
            imageUrl = item.imageUrl,
            quantity = item.quantity,
            unitPriceFormatted = currencyFormatter.format(item.unitPrice),
            lineTotalFormatted = currencyFormatter.format(item.lineTotal),
        )

        is CartItem.Pizza -> {
            val subtitle = item.toppings
                .filter { it.quantity > 0 }
                .map { "${it.quantity} x ${it.name}" }

            CartLineDisplayModel(
                lineId = item.lineId,
                productId = item.productId,
                name = item.name,
                subtitleLines = subtitle,
                imageUrl = item.imageUrl,
                quantity = item.quantity,
                unitPriceFormatted = currencyFormatter.format(item.unitPrice),
                lineTotalFormatted = currencyFormatter.format(item.lineTotal),
            )
        }
    }

    private fun mapToRecommendedItemDisplayModel(item: MenuItem) = RecommendedItemDisplayModel(
        id = item.id,
        name = item.name,
        unitPrice = item.unitPrice,
        unitPriceFormatted = currencyFormatter.format(item.unitPrice),
        imageUrl = item.imageUrl,
        category = item.category,
    )

    fun mapToCartItem(item: RecommendedItemDisplayModel) = CartItem.Other(
        lineId = item.id,
        productId = item.id,
        name = item.name,
        imageUrl = item.imageUrl,
        unitPrice = item.unitPrice,
        category = item.category,
        quantity = 1,
    )
}
