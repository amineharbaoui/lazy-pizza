package com.example.order.domain.usecase

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class GetPickupScheduleUseCaseTest {

    @Test
    fun `invoke when today has slots left then returns today and the next 6 days`() {
        // Given
        val useCase = GetPickupScheduleUseCase(clockAt(TODAY.atTime(NOON)))

        // When
        val schedule = useCase()

        // Then
        assertThat(schedule.today).isEqualTo(TODAY)
        assertThat(schedule.days.map { it.date }).containsExactly(
            TODAY,
            TODAY.plusDays(1),
            TODAY.plusDays(2),
            TODAY.plusDays(3),
            TODAY.plusDays(4),
            TODAY.plusDays(5),
            TODAY.plusDays(6),
        )
    }

    @Test
    fun `invoke when a day is not today then returns a slot every 15 minutes from opening until closing`() {
        // Given
        val useCase = GetPickupScheduleUseCase(clockAt(TODAY.atTime(NOON)))

        // When
        val tomorrow = useCase().days[1]

        // Then
        assertThat(tomorrow.timeSlots).hasSize(SLOTS_PER_FULL_DAY)
        assertThat(tomorrow.timeSlots.first()).isEqualTo(FIRST_SLOT)
        assertThat(tomorrow.timeSlots[1]).isEqualTo(SECOND_SLOT)
        assertThat(tomorrow.timeSlots.last()).isEqualTo(LAST_SLOT)
    }

    @ParameterizedTest(name = "at {0} today starts at {1}")
    @CsvSource(
        "07:00, 09:00",
        "08:44, 09:00",
        "08:45, 09:15",
        "11:59, 12:15",
        "12:00, 12:30",
        "12:01, 12:30",
        "22:29, 22:45",
    )
    fun `invoke when it is today then returns only the slots more than 15 minutes from now`(
        now: LocalTime,
        expectedFirstSlot: LocalTime,
    ) {
        // Given
        val useCase = GetPickupScheduleUseCase(clockAt(TODAY.atTime(now)))

        // When
        val today = useCase().days.first()

        // Then
        assertThat(today.date).isEqualTo(TODAY)
        assertThat(today.timeSlots.first()).isEqualTo(expectedFirstSlot)
        assertThat(today.timeSlots.last()).isEqualTo(LAST_SLOT)
    }

    @ParameterizedTest(name = "at {0} today is dropped")
    @CsvSource("22:30", "23:00", "23:44")
    fun `invoke when today has no slot left then returns the next 6 days only`(now: LocalTime) {
        // Given
        val useCase = GetPickupScheduleUseCase(clockAt(TODAY.atTime(now)))

        // When
        val schedule = useCase()

        // Then
        assertThat(schedule.today).isEqualTo(TODAY)
        assertThat(schedule.days.map { it.date }).containsExactly(
            TODAY.plusDays(1),
            TODAY.plusDays(2),
            TODAY.plusDays(3),
            TODAY.plusDays(4),
            TODAY.plusDays(5),
            TODAY.plusDays(6),
        )
    }

    @ParameterizedTest(name = "at {0} the earliest slot is {1}")
    @CsvSource(
        "07:00, 2026-11-12T09:00",
        "12:00, 2026-11-12T12:30",
        "22:29, 2026-11-12T22:45",
        "22:31, 2026-11-13T09:00",
        "23:00, 2026-11-13T09:00",
    )
    fun `invoke when it is a given time then returns the first slot of the first pickup day as the earliest slot`(
        now: LocalTime,
        expectedEarliestSlot: LocalDateTime,
    ) {
        // Given
        val useCase = GetPickupScheduleUseCase(clockAt(TODAY.atTime(now)))

        // When
        val schedule = useCase()

        // Then
        assertThat(schedule.earliestSlot).isEqualTo(expectedEarliestSlot)
    }

    private fun clockAt(now: LocalDateTime): Clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)

    private companion object {
        const val SLOTS_PER_FULL_DAY = 56
        val TODAY: LocalDate = LocalDate.of(2026, 11, 12)
        val NOON: LocalTime = LocalTime.of(12, 0)
        val FIRST_SLOT: LocalTime = LocalTime.of(9, 0)
        val SECOND_SLOT: LocalTime = LocalTime.of(9, 15)
        val LAST_SLOT: LocalTime = LocalTime.of(22, 45)
    }
}
