package com.example.order.domain.usecase

import com.example.order.domain.model.PickupDay
import com.example.order.domain.model.PickupSchedule
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class GetPickupScheduleUseCase @Inject constructor(
    private val clock: Clock,
) {
    operator fun invoke(): PickupSchedule {
        val now = LocalDateTime.now(clock)
        val today = now.toLocalDate()
        val days = (0 until PICKUP_DAY_COUNT)
            .map { offset -> today.plusDays(offset.toLong()) }
            .map { date -> PickupDay(date = date, timeSlots = timeSlots(date = date, now = now)) }
            .filter { day -> day.timeSlots.isNotEmpty() }

        return PickupSchedule(
            today = today,
            days = days,
            earliestSlot = days.firstOrNull()?.let { day -> earliestSlot(day = day, now = now.toLocalTime()) },
        )
    }

    private fun timeSlots(
        date: LocalDate,
        now: LocalDateTime,
    ): List<LocalTime> {
        val isToday = date == now.toLocalDate()
        val earliestTime = now.toLocalTime().plusMinutes(MIN_LEAD_MINUTES)

        return generateSequence(OPENING_TIME) { slot -> slot.plusMinutes(SLOT_MINUTES) }
            .takeWhile { slot -> slot.isBefore(CLOSING_TIME) }
            .filter { slot -> !isToday || slot.isAfter(earliestTime) }
            .toList()
    }

    private fun earliestSlot(
        day: PickupDay,
        now: LocalTime,
    ): LocalDateTime {
        val earliestTime = now.plusMinutes(MIN_LEAD_MINUTES)
        val slot = day.timeSlots.firstOrNull { slot -> !slot.isBefore(earliestTime) } ?: day.timeSlots.first()
        return day.date.atTime(slot)
    }

    private companion object {
        const val PICKUP_DAY_COUNT = 7
        const val SLOT_MINUTES = 15L
        const val MIN_LEAD_MINUTES = 15L
        val OPENING_TIME: LocalTime = LocalTime.of(9, 0)
        val CLOSING_TIME: LocalTime = LocalTime.of(23, 0)
    }
}
