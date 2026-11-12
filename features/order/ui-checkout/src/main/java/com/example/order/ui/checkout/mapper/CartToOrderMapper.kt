package com.example.order.ui.checkout.mapper

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartTopping
import com.example.order.domain.model.Order
import com.example.order.domain.model.OrderItem
import com.example.order.domain.model.OrderTopping
import com.example.order.domain.model.PickupRequest
import com.example.order.domain.usecase.CreateOrderUseCase
import com.example.order.ui.checkout.CheckoutUiState
import com.example.order.ui.checkout.PickupOption
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class CartToOrderMapper @Inject constructor(
    private val createOrderUseCase: CreateOrderUseCase,
) {

    fun map(
        cart: Cart,
        userId: String,
        checkout: CheckoutUiState.ReadyToOrder,
    ): Order = createOrderUseCase(
        userId = userId,
        pickup = mapPickupRequest(checkout),
        comment = checkout.comment,
        items = cart.items.map(::mapCartItem),
        total = cart.subtotal,
    )

    private fun mapPickupRequest(checkout: CheckoutUiState.ReadyToOrder): PickupRequest = when (checkout.pickup.selectedOption) {
        PickupOption.ASAP -> PickupRequest.Asap

        PickupOption.SCHEDULE -> {
            val (dayId, slotId) = resolveScheduledSelection(checkout)
            PickupRequest.Scheduled(at = LocalDate.parse(dayId).atTime(LocalTime.parse(slotId)))
        }
    }

    /**
     * Uses confirmation if present, else uses current selection.
     * Throws if missing because placing an order without a schedule selection is a bug.
     */
    private fun resolveScheduledSelection(checkout: CheckoutUiState.ReadyToOrder): Pair<String, String> {
        checkout.pickup.scheduleSheet.confirmation?.let { c ->
            return c.dayId to c.timeSlotId
        }

        val sel = checkout.pickup.scheduleSheet.selection
        val dayId = requireNotNull(sel.selectedDayId) { "Missing selectedDayId for scheduled pickup" }
        val slotId = requireNotNull(sel.selectedTimeSlotId) { "Missing selectedTimeSlotId for scheduled pickup" }
        return dayId to slotId
    }

    private fun mapCartItem(item: CartItem): OrderItem = when (item) {
        is CartItem.Pizza -> OrderItem(
            productId = item.productId,
            name = item.name,
            unitPrice = item.unitPrice,
            quantity = item.quantity,
            category = item.category,
            toppings = item.toppings.map(::mapTopping),
        )

        is CartItem.Other -> OrderItem(
            productId = item.productId,
            name = item.name,
            unitPrice = item.unitPrice,
            quantity = item.quantity,
            category = item.category,
            toppings = emptyList(),
        )
    }

    private fun mapTopping(topping: CartTopping): OrderTopping = OrderTopping(
        id = topping.id,
        name = topping.name,
        unitPrice = topping.unitPrice,
        quantity = topping.quantity,
    )
}
