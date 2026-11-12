package com.example.order.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class PickupSchedule(
    val today: LocalDate,
    val days: List<PickupDay>,
    val earliestSlot: LocalDateTime?,
)

data class PickupDay(
    val date: LocalDate,
    val timeSlots: List<LocalTime>,
)
