package com.example.order.ui.checkout.mapper

import com.example.cart.domain.model.Cart
import com.example.order.domain.model.PickupDay
import com.example.order.domain.model.PickupSchedule
import com.example.order.ui.checkout.CheckoutUiState
import com.example.order.ui.checkout.PickupDayUiModel
import com.example.order.ui.checkout.PickupOption
import com.example.order.ui.checkout.PickupOptionCardUiModel
import com.example.order.ui.checkout.PickupSelectionUiModel
import com.example.order.ui.checkout.PickupTimeSlotUiModel
import com.example.order.ui.checkout.PickupUiState
import com.example.order.ui.checkout.SchedulePickupUiModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class CheckoutUiStateFactory @Inject constructor(
    private val cartToOrderSummaryUiMapper: CartToOrderSummaryUiMapper,
) {

    fun createInitialReadyState(
        cart: Cart,
        schedule: PickupSchedule,
    ): CheckoutUiState.ReadyToOrder {
        val labels = PickupLabels(today = schedule.today, locale = Locale.getDefault())
        val days = schedule.days.map { it.toUiModel(labels) }
        val earliestSlot = schedule.earliestSlot

        val asapCard = PickupOptionCardUiModel(
            id = PickupOption.ASAP,
            title = "Earliest available",
            dateLabel = earliestSlot?.let { "${labels.dayLabel(it.toLocalDate())} - ${labels.dateLabel(it.toLocalDate())}" },
            timeLabel = earliestSlot?.let { labels.timeLabel(it.toLocalTime()) },
            isSelected = true,
            isEnabled = earliestSlot != null,
        )

        val scheduleCard = PickupOptionCardUiModel(
            id = PickupOption.SCHEDULE,
            title = "Schedule",
            dateLabel = "Choose a time",
            timeLabel = null,
            isSelected = false,
            isEnabled = days.isNotEmpty(),
        )

        return CheckoutUiState.ReadyToOrder(
            pickup = PickupUiState(
                selectedOption = PickupOption.ASAP,
                asapCard = asapCard,
                scheduleCard = scheduleCard,
                scheduleSheet = SchedulePickupUiModel(
                    days = days,
                    selection = PickupSelectionUiModel(
                        selectedDayId = null,
                        selectedTimeSlotId = null,
                    ),
                    confirmation = null,
                ),
            ),
            orderSummary = cartToOrderSummaryUiMapper.map(cart),
            comment = "",
            canPlaceOrder = true,
            isPlacingOrder = false,
        )
    }

    private fun PickupDay.toUiModel(labels: PickupLabels): PickupDayUiModel = PickupDayUiModel(
        id = date.toString(),
        dayLabel = labels.dayLabel(date),
        dateLabel = labels.dateLabel(date),
        isEnabled = true,
        timeSlots = timeSlots.map { slot ->
            PickupTimeSlotUiModel(
                id = slot.toString(),
                timeLabel = labels.timeLabel(slot),
                isEnabled = true,
            )
        },
    )

    private class PickupLabels(
        private val today: LocalDate,
        locale: Locale,
    ) {
        private val dayNameFormatter = DateTimeFormatter.ofPattern("EEE", locale)
        private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", locale)
        private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", locale)

        fun dayLabel(date: LocalDate): String = when (date) {
            today -> "Today"
            today.plusDays(1) -> "Tomorrow"
            else -> date.format(dayNameFormatter)
        }

        fun dateLabel(date: LocalDate): String = date.format(dateFormatter)

        fun timeLabel(time: LocalTime): String = time.format(timeFormatter)
    }
}
