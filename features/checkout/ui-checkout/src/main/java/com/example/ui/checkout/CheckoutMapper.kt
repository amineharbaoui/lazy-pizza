package com.example.ui.checkout

import com.example.domain.model.Cart
import com.example.domain.model.CartItem
import com.example.domain.model.Order
import com.example.domain.model.OrderItem
import com.example.domain.model.OrderTopping
import com.example.domain.model.PickupSelection
import com.example.menu.utils.formatting.toFormattedCurrency
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Cart.toOrderSummaryUi(): OrderSummaryUi = OrderSummaryUi(
    lines = items.map { it.toOrderLineUi() },
    totalLabel = subtotal.toFormattedCurrency(),
)

fun Cart.toOrder(
    userId: String,
    checkout: CheckoutUiState.Ready,
): Order {
    val pickup = when (checkout.pickup.selectedOption) {
        PickupOption.ASAP -> PickupSelection.Asap(estimatedMinutes = 15)
        PickupOption.SCHEDULE -> {
            val confirmation = checkout.pickup.schedule.confirmation
                ?: error("Schedule selected but not confirmed")
            val day = checkout.pickup.schedule.days.first { it.id == confirmation.dayId }
            val slot = day.availableTimeSlots.first { it.id == confirmation.timeSlotId }

            PickupSelection.Scheduled(
                dayId = confirmation.dayId,
                timeSlotId = confirmation.timeSlotId,
                timeSlotLabel = slot.label,
            )
        }
    }

    return Order(
        userId = userId,
        createdAt = Instant.now(),
        pickup = pickup,
        comment = checkout.comment,
        total = subtotal,
        items = items.map { it.toOrderItem() },
    )
}

private fun CartItem.toOrderItem(): OrderItem = when (this) {
    is CartItem.Pizza -> OrderItem.Pizza(
        productId = productId,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity,
        toppings = toppings.map {
            OrderTopping(
                id = it.id,
                name = it.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
            )
        },
        category = category,
    )

    is CartItem.Other -> OrderItem.Other(
        productId = productId,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity,
        category = category,
    )
}

fun CheckoutUiState.Ready.updatePickupSelection(
    dayId: String?,
    timeSlotId: String?,
): CheckoutUiState.Ready = copy(
    pickup = pickup.copy(
        schedule = pickup.schedule.copy(
            selection = PickUpSelection(dayId, timeSlotId),
        ),
    ),
)

fun CheckoutUiState.Ready.updatePickupOption(option: PickupOption): CheckoutUiState.Ready = copy(
    pickup = pickup.copy(selectedOption = option),
)

fun CheckoutUiState.Ready.confirmPickupSchedule(
    dayId: String,
    dayLabelTop: String?,
    dayLabelBottom: String?,
    timeSlotId: String,
    timeSlotLabel: String,
): CheckoutUiState.Ready = copy(
    pickup = pickup.copy(
        selectedOption = PickupOption.SCHEDULE,
        scheduleOption = pickup.scheduleOption.copy(
            dateLabel = "$dayLabelTop - $dayLabelBottom",
            timeLabel = timeSlotLabel,
        ),
        schedule = pickup.schedule.copy(
            confirmation = PickUpConfirmation(
                dayId = dayId,
                timeSlotId = timeSlotId,
            ),
        ),
    ),
)

private fun CartItem.toOrderLineUi(): OrderLineUi = when (this) {
    is CartItem.Pizza -> OrderLineUi.MainProduct(
        productId = productId,
        title = "${quantity}x $name",
        unitPriceLabel = unitPrice.toFormattedCurrency(),
        subtitleLines = toppings.map { "${it.quantity}x ${it.name} (${it.unitPrice.toFormattedCurrency()})" },
        totalPriceLabel = lineTotal.toFormattedCurrency().takeIf { toppings.isNotEmpty() },
    )

    is CartItem.Other -> OrderLineUi.SecondaryProduct(
        title = "${quantity}x $name",
        unitPriceLabel = (unitPrice * quantity).toFormattedCurrency(),
        totalPriceLabel = null,
        subtitleLines = emptyList(),
    )
}

fun createInitialReadyState(cart: Cart): CheckoutUiState.Ready {
    val currentTime = LocalTime.now()
    val asapTimeLabel = currentTime.plusMinutes(15).format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
    val days = generatePickupDays()
    val today = days.firstOrNull()

    return CheckoutUiState.Ready(
        pickup = PickupUiState(
            selectedOption = PickupOption.ASAP,
            asapOption = PickupOptionDisplayModel(
                title = "Earliest available",
                dateLabel = "Today - ${today?.labelBottom}",
                timeLabel = asapTimeLabel,
            ),
            scheduleOption = PickupOptionDisplayModel(
                title = "Schedule",
                dateLabel = "Choose a time",
                timeLabel = null,
            ),
            schedule = SchedulePickUpDisplayModel(
                days = days,
                selection = PickUpSelection(
                    selectedDayId = null,
                    selectedTimeSlotId = null,
                ),
                confirmation = null,
            ),
        ),
        orderSummary = cart.toOrderSummaryUi(),
        comment = "",
        canPlaceOrder = true,
    )
}

fun generatePickupDays(count: Int = 7): List<PickUpDay> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
    val dayNameFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

    return (0 until count).map { i ->
        val date = today.plusDays(i.toLong())
        val labelTop = when (i) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> date.format(dayNameFormatter)
        }
        PickUpDay(
            id = date.toString(), // e.g., "2023-10-27"
            labelTop = labelTop,
            labelBottom = date.format(formatter),
            availableTimeSlots = generateTimeSlots(date),
        )
    }.filter { it.availableTimeSlots.isNotEmpty() }
}

fun generateTimeSlots(selectedDate: LocalDate): List<PickUpTimeSlot> {
    val now = LocalTime.now()
    val isToday = selectedDate == LocalDate.now()

    val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
    val slots = mutableListOf<PickUpTimeSlot>()

    // Start from the beginning of the day (e.g., 9 AM) or now
    var current = LocalTime.of(9, 0)
    val endTime = LocalTime.of(23, 0) // End at 11 PM

    while (current.isBefore(endTime)) {
        val next = current.plusMinutes(15)

        // If it's today, skip slots that have already passed
        val isFuture = !isToday || current.isAfter(now.plusMinutes(15)) // 15m buffer for prep

        if (isFuture) {
            val label = "${current.format(formatter)} - ${next.format(formatter)}"
            slots.add(PickUpTimeSlot(id = current.toString(), label = label))
        }
        current = next
    }
    return slots
}
