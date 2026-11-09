package com.example.order.domain.usecase

import app.cash.turbine.test
import com.example.core.model.OrderStatus
import com.example.order.domain.model.Order
import com.example.order.domain.model.PickupType
import com.example.order.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ObserveOrdersUseCaseTest {

    private val clock = Clock.fixed(NOW, ZoneOffset.UTC)

    @ParameterizedTest(name = "{0} with pickup {1} minutes from now emits {2}")
    @CsvSource(
        "IN_PROGRESS, -1, COMPLETED",
        "IN_PROGRESS, 0, IN_PROGRESS",
        "IN_PROGRESS, 1, IN_PROGRESS",
        "CANCELED, -1, CANCELED",
        "COMPLETED, 1, COMPLETED",
    )
    fun `invoke when order has a status and a pickup time then emits its status at the clock time`(
        status: OrderStatus,
        pickupMinutesFromNow: Long,
        expected: OrderStatus,
    ) = runTest {
        // Given
        val order = order(status = status, pickupAt = NOW.plusSeconds(pickupMinutesFromNow * SECONDS_PER_MINUTE))
        val useCase = ObserveOrdersUseCase(InMemoryOrdersRepository(listOf(order)), clock)

        // When
        useCase(USER_ID).test {
            // Then
            assertThat(awaitItem().single().status).isEqualTo(expected)
            awaitComplete()
        }
    }

    private class InMemoryOrdersRepository(private val orders: List<Order>) : OrdersRepository {
        override suspend fun placeOrder(order: Order): Result<Unit> = Result.success(Unit)
        override fun observeMyOrders(userId: String): Flow<List<Order>> = flowOf(orders)
    }

    private fun order(
        status: OrderStatus,
        pickupAt: Instant,
    ) = Order(
        orderNumber = ORDER_NUMBER,
        userId = USER_ID,
        createdAt = NOW,
        pickupType = PickupType.SCHEDULED,
        pickupAt = pickupAt,
        status = status,
        comment = "",
        items = emptyList(),
        total = 0.0,
    )

    private companion object {
        const val USER_ID = "user-123"
        const val ORDER_NUMBER = "order-1"
        const val SECONDS_PER_MINUTE = 60L
        val NOW: Instant = Instant.parse("2026-11-09T12:00:00Z")
    }
}
