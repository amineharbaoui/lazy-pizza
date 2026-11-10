package com.example.order.ui.pastorders

import app.cash.turbine.test
import com.example.auth.data.AuthTestData
import com.example.auth.domain.usecase.ObserveCurrentUserUidUseCase
import com.example.core.model.OrderStatus
import com.example.core.model.ProductCategory
import com.example.core.testing.MainDispatcherExtension
import com.example.core.ui.utils.formatting.DefaultCurrencyFormatter
import com.example.order.data.OrderTestData
import com.example.order.domain.model.Order
import com.example.order.domain.model.OrderItem
import com.example.order.domain.model.PickupType
import com.example.order.domain.usecase.ObserveOrdersUseCase
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class OrdersViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val authTestData = AuthTestData()
    private val orderTestData = OrderTestData()

    private fun viewModel() = OrdersViewModel(
        observeCurrentUserUidUseCase = ObserveCurrentUserUidUseCase(authTestData.sessionRepository),
        observeOrdersUseCase = ObserveOrdersUseCase(orderTestData.ordersRepository, Clock.fixed(NOW, ZoneOffset.UTC)),
        orderToUiMapper = OrderToUiMapper(DefaultCurrencyFormatter()),
    )

    @Nested
    inner class UiState {

        @Test
        fun `uiState when the user signs in then shows their orders`() = runTest {
            // Given
            authTestData.givenSignedOut()
            orderTestData.givenOrders(order(userId = USER_ID))
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(OrderUiState.Loading)
                assertThat(awaitItem()).isEqualTo(OrderUiState.NotLoggedIn)

                // When
                authTestData.givenSignedIn(USER_ID)

                // Then
                val state = awaitItem()
                assertThat(state).isInstanceOf(OrderUiState.Ready::class.java)
                assertThat((state as OrderUiState.Ready).orders.map { it.orderNumber }).containsExactly(ORDER_NUMBER)
            }
        }

        @Test
        fun `uiState when the user signs out then shows not logged in`() = runTest {
            // Given
            authTestData.givenSignedIn(USER_ID)
            orderTestData.givenOrders(order(userId = USER_ID))
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(OrderUiState.Loading)
                assertThat(awaitItem()).isInstanceOf(OrderUiState.Ready::class.java)

                // When
                authTestData.givenSignedOut()

                // Then
                assertThat(awaitItem()).isEqualTo(OrderUiState.NotLoggedIn)
            }
        }

        @Test
        fun `uiState when another user signs in then shows only their orders`() = runTest {
            // Given
            authTestData.givenSignedIn(USER_ID)
            orderTestData.givenOrders(
                order(userId = USER_ID),
                order(userId = OTHER_USER_ID, orderNumber = OTHER_ORDER_NUMBER),
            )
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(OrderUiState.Loading)
                assertThat(awaitItem()).isInstanceOf(OrderUiState.Ready::class.java)

                // When
                authTestData.givenSignedIn(OTHER_USER_ID)

                // Then
                val state = awaitItem()
                assertThat(state).isInstanceOf(OrderUiState.Ready::class.java)
                assertThat((state as OrderUiState.Ready).orders.map { it.orderNumber }).containsExactly(OTHER_ORDER_NUMBER)
            }
        }
    }

    private fun order(
        userId: String,
        orderNumber: String = ORDER_NUMBER,
    ) = Order(
        orderNumber = orderNumber,
        userId = userId,
        createdAt = NOW,
        pickupType = PickupType.ASAP,
        pickupAt = NOW,
        status = OrderStatus.COMPLETED,
        comment = "",
        items = listOf(
            OrderItem(
                productId = "cola",
                name = "Cola",
                unitPrice = 1.99,
                quantity = 1,
                category = ProductCategory.DRINK,
            ),
        ),
        total = 1.99,
    )

    private companion object {
        const val USER_ID = "user-1"
        const val ORDER_NUMBER = "12345"
        const val OTHER_USER_ID = "user-2"
        const val OTHER_ORDER_NUMBER = "67890"
        val NOW: Instant = Instant.parse("2026-11-10T12:00:00Z")
    }
}
