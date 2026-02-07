package com.example.order.ui.checkout.mapper

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartTopping
import com.example.core.model.OrderStatus
import com.example.order.domain.model.Order
import com.example.order.domain.model.OrderItem
import com.example.order.domain.model.OrderTopping
import com.example.order.domain.model.PickupType
import com.example.order.ui.checkout.CheckoutUiState
import com.example.order.ui.checkout.PickupOption
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CartToOrderMapper @Inject constructor() {

    private companion object {
        private val ZONE_ID: ZoneId = ZoneId.systemDefault()
        private const val ASAP_PREP_MINUTES: Long = 30L
        private const val ASAP_ROUND_TO_MINUTES: Long = 30L
    }

    fun map(
        cart: Cart,
        userId: String,
        checkout: CheckoutUiState.ReadyToOrder,
    ): Order {
        val (pickupType, pickupAt) = mapPickupDetails(checkout)

        return Order(
            orderNumber = System.currentTimeMillis().toString().takeLast(6),
            userId = userId,
            createdAt = Instant.now(),
            pickupType = pickupType,
            pickupAt = pickupAt,
            comment = checkout.comment,
            total = cart.subtotal,
            status = OrderStatus.IN_PROGRESS,
            items = cart.items.map(::mapCartItem),
        )
    }

    private fun mapPickupDetails(checkout: CheckoutUiState.ReadyToOrder): Pair<PickupType, Instant> = when (checkout.pickup.selectedOption) {
        PickupOption.ASAP -> {
            val pickupAt = computeAsapPickupInstant()
            PickupType.ASAP to pickupAt
        }

        PickupOption.SCHEDULE -> {
            val (dayId, slotId) = resolveScheduledSelection(checkout)
            val pickupAt = toInstant(dayId = dayId, timeSlotId = slotId)
            PickupType.SCHEDULED to pickupAt
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

    /**
     * ASAP: now + prep, then rounded up to the next X minutes (default 15m).
     * Example: 12:07 + 15m = 12:22 -> rounded to 12:30.
     */
    private fun computeAsapPickupInstant(): Instant {
        val now = ZonedDateTime.now(ZONE_ID)
        val withPrep = now.plusMinutes(ASAP_PREP_MINUTES)

        val minute = withPrep.minute
        val remainder = minute % ASAP_ROUND_TO_MINUTES.toInt()
        val rounded = if (remainder == 0) {
            withPrep.truncatedTo(ChronoUnit.MINUTES)
        } else {
            withPrep
                .plusMinutes((ASAP_ROUND_TO_MINUTES - remainder))
                .withSecond(0)
                .withNano(0)
        }

        return rounded.toInstant()
    }

    /**
     * dayId: "yyyy-MM-dd"
     * timeSlotId: "HH:mm" (e.g. "17:15")
     */
    private fun toInstant(
        dayId: String,
        timeSlotId: String,
    ): Instant {
        val date = LocalDate.parse(dayId)
        val time = LocalTime.parse(timeSlotId)
        return ZonedDateTime.of(date, time, ZONE_ID).toInstant()
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
