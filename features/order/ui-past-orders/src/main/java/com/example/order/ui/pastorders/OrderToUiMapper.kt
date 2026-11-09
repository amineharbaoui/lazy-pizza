package com.example.order.ui.pastorders

import com.example.core.ui.utils.formatting.CurrencyFormatter
import com.example.core.ui.utils.formatting.formatForOrderCard
import com.example.order.domain.model.Order
import javax.inject.Inject

class OrderToUiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) {
    fun map(order: Order): OrderDisplayModel = OrderDisplayModel(
        orderNumber = order.orderNumber,
        dateTimeLabel = formatForOrderCard(order.createdAt.toEpochMilli()),
        items = order.items.map { item ->
            OrderItemDisplayModel(
                quantity = item.quantity,
                name = item.name,
                toppings = item.toppings.map { OrderToppingDisplayModel(quantity = it.quantity, name = it.name) },
            )
        },
        totalAmountLabel = currencyFormatter.format(order.total),
        status = order.status,
    )
}
