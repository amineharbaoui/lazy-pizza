package com.example.checkout.ui.past.orders

import com.example.domain.model.Order
import com.example.menu.utils.formatting.CurrencyFormatter
import com.example.menu.utils.formatting.formatForOrderCard
import javax.inject.Inject

class OrderToUiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) {
    fun map(order: Order): OrderUi = OrderUi(
        orderNumberLabel = "Order #${order.orderNumber}",
        dateTimeLabel = formatForOrderCard(order.createdAt.toEpochMilli()),
        items = order.items.map { item ->
            OrderItem(
                name = "${item.quantity} x ${item.name}",
                toppings = item.toppings.map { "${it.quantity} x ${it.name}" },
            )
        },
        totalAmountLabel = currencyFormatter.format(order.total),
        status = order.status,
    )
}
