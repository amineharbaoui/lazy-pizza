package com.example.ui.checkout.mapper

import CheckoutUiState
import PickupDayUiModel
import PickupOption
import PickupOptionCardUiModel
import PickupSelectionUiModel
import PickupTimeSlotUiModel
import PickupUiState
import SchedulePickupUiModel
import com.example.domain.model.Cart
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class CheckoutUiStateFactory @Inject constructor(
    private val cartToOrderSummaryUiMapper: CartToOrderSummaryUiMapper,
) {

    fun createInitialReadyState(cart: Cart): CheckoutUiState.ReadyToOrder {
        val days = generatePickupDays()
        val today = days.firstOrNull()

        val asapSlot = findAsapSlot(today)

        val asapDateLabel = today?.let { "${it.dayLabel} - ${it.dateLabel}" }
        val asapTimeLabel = asapSlot?.timeLabel

        val asapCard = PickupOptionCardUiModel(
            id = PickupOption.ASAP,
            title = "Earliest available",
            dateLabel = asapDateLabel,
            timeLabel = asapTimeLabel,
            isSelected = true,
            isEnabled = asapSlot != null,
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

    private fun findAsapSlot(today: PickupDayUiModel?): PickupTimeSlotUiModel? {
        if (today == null) return null

        val nowPlus15 = LocalTime.now().plusMinutes(15)
        val candidates = today.timeSlots

        return candidates.firstOrNull { slot ->
            runCatching { LocalTime.parse(slot.id) }.getOrNull()?.let { !it.isBefore(nowPlus15) } ?: false
        } ?: candidates.firstOrNull()
    }

    private fun generatePickupDays(count: Int = 7): List<PickupDayUiModel> {
        val today = LocalDate.now()
        val subtitleFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
        val dayNameFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

        return (0 until count).mapNotNull { i ->
            val date = today.plusDays(i.toLong())

            val title = when (i) {
                0 -> "Today"
                1 -> "Tomorrow"
                else -> date.format(dayNameFormatter)
            }

            val timeSlots = generateTimeSlots(date)
            if (timeSlots.isEmpty()) return@mapNotNull null

            PickupDayUiModel(
                id = date.toString(),
                dayLabel = title,
                dateLabel = date.format(subtitleFormatter),
                isEnabled = true,
                timeSlots = timeSlots,
            )
        }
    }

    private fun generateTimeSlots(selectedDate: LocalDate): List<PickupTimeSlotUiModel> {
        val now = LocalTime.now()
        val isToday = selectedDate == LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

        val slots = mutableListOf<PickupTimeSlotUiModel>()

        var current = LocalTime.of(9, 0)
        val endTime = LocalTime.of(23, 0)

        while (current.isBefore(endTime)) {
            val isFuture = !isToday || current.isAfter(now.plusMinutes(15))
            if (isFuture) {
                slots += PickupTimeSlotUiModel(
                    id = current.toString(),
                    timeLabel = current.format(formatter),
                    isEnabled = true,
                )
            }
            current = current.plusMinutes(15)
        }

        return slots
    }
}
