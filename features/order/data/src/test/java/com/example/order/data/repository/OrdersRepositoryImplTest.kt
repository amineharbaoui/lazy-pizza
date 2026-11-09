package com.example.order.data.repository

import app.cash.turbine.test
import com.example.core.model.OrderStatus
import com.example.order.data.OrderTestData
import com.example.order.domain.model.Order
import com.example.order.domain.model.PickupType
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class OrdersRepositoryImplTest {

    private val testData = OrderTestData()

    @Test
    fun `observeMyOrders when user has orders then emits them newest first`() = runTest {
        // Given
        val older = order(orderNumber = OLDER_ORDER, createdAt = NOW.minusSeconds(SECONDS_PER_HOUR))
        val newer = order(orderNumber = NEWER_ORDER, createdAt = NOW, status = OrderStatus.CANCELED)
        val otherUsers = order(orderNumber = OTHER_USERS_ORDER, userId = OTHER_USER_ID)
        testData.givenOrders(older, otherUsers, newer)

        // When
        testData.ordersRepository.observeMyOrders(USER_ID).test {
            // Then
            assertThat(awaitItem()).containsExactly(newer, older)
        }
    }

    @Test
    fun `observeMyOrders when the data source fails then throws`() = runTest {
        // Given
        val error = IllegalStateException(FAILURE)
        testData.givenFailure(error)

        // When
        testData.ordersRepository.observeMyOrders(USER_ID).test {
            // Then
            assertThat(awaitError()).isSameAs(error)
        }
    }

    private fun order(
        orderNumber: String,
        userId: String = USER_ID,
        createdAt: Instant = NOW,
        status: OrderStatus = OrderStatus.COMPLETED,
    ) = Order(
        orderNumber = orderNumber,
        userId = userId,
        createdAt = createdAt,
        pickupType = PickupType.ASAP,
        pickupAt = createdAt,
        status = status,
        comment = "",
        items = emptyList(),
        total = TOTAL,
    )

    private companion object {
        const val USER_ID = "user-123"
        const val OTHER_USER_ID = "user-456"
        const val OLDER_ORDER = "order-1"
        const val NEWER_ORDER = "order-2"
        const val OTHER_USERS_ORDER = "order-3"
        const val FAILURE = "Forced failure"
        const val TOTAL = 12.5
        const val SECONDS_PER_HOUR = 3600L
        val NOW: Instant = Instant.parse("2026-11-09T12:00:00Z")
    }
}
