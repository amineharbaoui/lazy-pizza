package com.example.order.ui.checkout.mapper

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.core.ui.utils.formatting.CurrencyFormatter
import com.example.order.ui.checkout.OrderLineDisplayModel
import com.example.order.ui.checkout.OrderSummaryDisplayModel
import javax.inject.Inject

class CartToOrderSummaryDisplayModelMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) {

    fun map(cart: Cart): OrderSummaryDisplayModel = OrderSummaryDisplayModel(
        lines = cart.items.map { mapCartItem(it) },
        totalLabel = currencyFormatter.format(cart.subtotal),
    )

    private fun mapCartItem(item: CartItem): OrderLineDisplayModel = when (item) {
        is CartItem.Pizza -> OrderLineDisplayModel.PizzaProduct(
            productId = item.productId,
            title = "${item.quantity}x ${item.name}",
            unitPriceLabel = currencyFormatter.format(item.unitPrice),
            subtitleLines = item.toppings.map { topping ->
                "${topping.quantity}x ${topping.name} (${currencyFormatter.format(topping.unitPrice)})"
            },
            totalPriceLabel = if (item.toppings.isNotEmpty()) {
                currencyFormatter.format(item.lineTotal)
            } else {
                null
            },
        )

        is CartItem.Other -> OrderLineDisplayModel.OtherProduct(
            title = "${item.quantity}x ${item.name}",
            unitPriceLabel = currencyFormatter.format(item.unitPrice * item.quantity),
            totalPriceLabel = null,
            subtitleLines = emptyList(),
        )
    }
}
