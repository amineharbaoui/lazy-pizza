package com.example.order.ui.checkout.mapper

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.menu.utils.formatting.CurrencyFormatter
import com.example.order.ui.checkout.OrderLineUi
import com.example.order.ui.checkout.OrderSummaryUi
import javax.inject.Inject

class CartToOrderSummaryUiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) {

    fun map(cart: Cart): OrderSummaryUi = OrderSummaryUi(
        lines = cart.items.map { mapCartItem(it) },
        totalLabel = currencyFormatter.format(cart.subtotal),
    )

    private fun mapCartItem(item: CartItem): OrderLineUi = when (item) {
        is CartItem.Pizza -> OrderLineUi.PizzaProduct(
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

        is CartItem.Other -> OrderLineUi.OtherProduct(
            title = "${item.quantity}x ${item.name}",
            unitPriceLabel = currencyFormatter.format(item.unitPrice * item.quantity),
            totalPriceLabel = null,
            subtitleLines = emptyList(),
        )
    }
}
