package com.example.order.domain.usecase

import com.example.core.model.OrderStatus
import com.example.core.model.ProductCategory
import com.example.order.domain.model.OrderItem
import com.example.order.domain.model.PickupRequest
import com.example.order.domain.model.PickupType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class CreateOrderUseCaseTest {

    @Test
    fun `invoke when an order is created then returns it in progress at the clock time with the given details`() {
        // Given
        val useCase = CreateOrderUseCase(Clock.fixed(NOW, ZoneOffset.UTC))

        // When
        val order = useCase(
            userId = USER_ID,
            pickup = PickupRequest.Asap,
            comment = COMMENT,
            items = listOf(ORDER_ITEM),
            total = TOTAL,
        )

        // Then
        assertThat(order.createdAt).isEqualTo(NOW)
        assertThat(order.status).isEqualTo(OrderStatus.IN_PROGRESS)
        assertThat(order.userId).isEqualTo(USER_ID)
        assertThat(order.comment).isEqualTo(COMMENT)
        assertThat(order.items).containsExactly(ORDER_ITEM)
        assertThat(order.total).isEqualTo(TOTAL)
    }

    @Test
    fun `invoke when an order is created then returns the last 6 digits of the clock time in milliseconds as its number`() {
        // Given
        val useCase = CreateOrderUseCase(Clock.fixed(NOW, ZoneOffset.UTC))

        // When
        val order = createOrder(useCase, PickupRequest.Asap)

        // Then
        assertThat(order.orderNumber).isEqualTo(ORDER_NUMBER)
    }

    @ParameterizedTest(name = "at {0} an ASAP pickup is at {1}")
    @CsvSource(
        "2026-11-12T12:00, 2026-11-12T12:30",
        "2026-11-12T12:01, 2026-11-12T13:00",
        "2026-11-12T12:29, 2026-11-12T13:00",
        "2026-11-12T12:30, 2026-11-12T13:00",
        "2026-11-12T12:31, 2026-11-12T13:30",
        "2026-11-12T23:45, 2026-11-13T00:30",
    )
    fun `invoke when pickup is asap then returns now plus 30 minutes rounded up to the next half hour`(
        now: LocalDateTime,
        expectedPickupAt: LocalDateTime,
    ) {
        // Given
        val useCase = CreateOrderUseCase(Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC))

        // When
        val order = createOrder(useCase, PickupRequest.Asap)

        // Then
        assertThat(order.pickupType).isEqualTo(PickupType.ASAP)
        assertThat(order.pickupAt).isEqualTo(expectedPickupAt.toInstant(ZoneOffset.UTC))
    }

    @Test
    fun `invoke when pickup is scheduled then returns the scheduled time in the clock zone`() {
        // Given
        val useCase = CreateOrderUseCase(Clock.fixed(NOW, PARIS))

        // When
        val order = createOrder(useCase, PickupRequest.Scheduled(at = SCHEDULED_LOCAL_TIME))

        // Then
        assertThat(order.pickupType).isEqualTo(PickupType.SCHEDULED)
        assertThat(order.pickupAt).isEqualTo(SCHEDULED_INSTANT_IN_PARIS)
    }

    private fun createOrder(
        useCase: CreateOrderUseCase,
        pickup: PickupRequest,
    ) = useCase(
        userId = USER_ID,
        pickup = pickup,
        comment = COMMENT,
        items = listOf(ORDER_ITEM),
        total = TOTAL,
    )

    private companion object {
        const val USER_ID = "user-123"
        const val COMMENT = "No onions"
        const val TOTAL = 12.5
        const val ORDER_NUMBER = "800123"
        val NOW: Instant = Instant.ofEpochMilli(1_794_484_800_123)
        val PARIS: ZoneId = ZoneId.of("Europe/Paris")
        val SCHEDULED_LOCAL_TIME: LocalDateTime = LocalDateTime.of(2026, 11, 13, 18, 15)
        val SCHEDULED_INSTANT_IN_PARIS: Instant = Instant.parse("2026-11-13T17:15:00Z")
        val ORDER_ITEM = OrderItem(
            productId = "margherita",
            name = "Margherita",
            unitPrice = TOTAL,
            quantity = 1,
            category = ProductCategory.PIZZA,
        )
    }
}
