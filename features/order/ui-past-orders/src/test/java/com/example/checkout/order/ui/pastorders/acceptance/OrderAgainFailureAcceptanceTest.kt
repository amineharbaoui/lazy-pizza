package com.example.checkout.order.ui.pastorders.acceptance

import app.cash.turbine.Event
import app.cash.turbine.test
import com.example.checkout.order.ui.pastorders.OrderAgainErrorUi
import com.example.checkout.order.ui.pastorders.OrderAgainUiState
import com.example.checkout.order.ui.pastorders.OrdersUiEvent
import com.example.core.testing.MainDispatcherExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

// ORDER-17 to ORDER-21: Order again fails safely and can be retried.
class OrderAgainFailureAcceptanceTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private lateinit var wiring: OrderAgainTestWiring

    @BeforeEach
    fun setUp() {
        wiring = OrderAgainTestWiring(mainDispatcher)
    }

    @AfterEach
    fun tearDown() {
        wiring.closeOrdersScreen()
    }

    /** ORDER-17: cart holds 1 x Pepperoni, past order of 1 x Cola, menu fails to load. */
    private fun givenMenuFailsToLoad() {
        wiring.openOrdersScreenWith(pastCola(1), cart = listOf(cartPepperoni()))
        wiring.productRemoteDataSource.isFailing = true
    }

    /** ORDER-18: cart holds 1 x Pepperoni, past order of 1 x Cola, menu loads with no products. */
    private fun givenMenuComesBackEmpty() {
        wiring.openOrdersScreenWith(pastCola(1), cart = listOf(cartPepperoni()))
        wiring.productRemoteDataSource.setEmptyMenu()
    }

    /** ORDER-19: two-item order, so a non-atomic write would leave a partial cart. */
    private fun givenCartFailsToSave() {
        wiring.openOrdersScreenWith(pastMargherita(1), pastCola(1), cart = listOf(cartPepperoni()))
        wiring.cartLocalDataSource.failWritesAfterLines(1)
    }

    // ORDER-17

    @Test
    suspend fun `orderAgain when menu fails to load then emits menu unavailable error event`() {
        // Given
        givenMenuFailsToLoad()

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()

            // Then
            assertThat(awaitItem()).isEqualTo(OrdersUiEvent.ShowOrderAgainError(OrderAgainErrorUi.MENU_UNAVAILABLE))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    suspend fun `orderAgain when menu fails to load then leaves the cart unchanged`() {
        // Given
        givenMenuFailsToLoad()

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(pepperoniLine())
    }

    @Test
    suspend fun `orderAgain when menu fails to load then emits no navigation event`() {
        // Given
        givenMenuFailsToLoad()

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()

            // Then
            assertThat(cancelAndConsumeRemainingEvents()).doesNotContain(Event.Item(OrdersUiEvent.NavigateToCart))
        }
    }

    @Test
    fun `orderAgain when menu fails to load then shows order again buttons enabled`() {
        // Given
        givenMenuFailsToLoad()

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.Idle)
    }

    // ORDER-18

    @Test
    suspend fun `orderAgain when menu comes back empty then emits menu unavailable error event`() {
        // Given
        givenMenuComesBackEmpty()

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()

            // Then
            assertThat(awaitItem()).isEqualTo(OrdersUiEvent.ShowOrderAgainError(OrderAgainErrorUi.MENU_UNAVAILABLE))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `orderAgain when menu comes back empty then shows no dialog`() {
        // Given
        givenMenuComesBackEmpty()

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.Idle)
    }

    @Test
    suspend fun `orderAgain when menu comes back empty then leaves the cart unchanged`() {
        // Given
        givenMenuComesBackEmpty()

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(pepperoniLine())
    }

    // ORDER-19

    @Test
    suspend fun `orderAgain when cart fails to save then emits cart update failed error event`() {
        // Given
        givenCartFailsToSave()

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()

            // Then
            assertThat(awaitItem()).isEqualTo(OrdersUiEvent.ShowOrderAgainError(OrderAgainErrorUi.CART_UPDATE_FAILED))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    suspend fun `orderAgain when cart fails to save then leaves the cart unchanged`() {
        // Given
        givenCartFailsToSave()

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(pepperoniLine())
    }

    @Test
    suspend fun `orderAgain when cart fails to save then emits no navigation event`() {
        // Given
        givenCartFailsToSave()

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()

            // Then
            assertThat(cancelAndConsumeRemainingEvents()).doesNotContain(Event.Item(OrdersUiEvent.NavigateToCart))
        }
    }

    @Test
    fun `orderAgain when cart fails to save then shows order again buttons enabled`() {
        // Given
        givenCartFailsToSave()

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.Idle)
    }

    // ORDER-20

    @Test
    suspend fun `orderAgain when retried after menu failure then cart holds the order`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1))
        wiring.productRemoteDataSource.isFailing = true
        wiring.orderAgainAndSettle()
        wiring.productRemoteDataSource.isFailing = false

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(colaLine(quantity = 1))
    }

    @Test
    suspend fun `orderAgain when retried after menu failure then emits navigate to cart event`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1))
        wiring.productRemoteDataSource.isFailing = true
        wiring.orderAgainAndSettle()
        wiring.productRemoteDataSource.isFailing = false

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()

            // Then
            assertThat(awaitItem()).isEqualTo(OrdersUiEvent.NavigateToCart)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ORDER-21

    @Test
    suspend fun `orderAgain when retried after cart failure then cart holds the order`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1))
        wiring.cartLocalDataSource.failWritesAfterLines(0)
        wiring.orderAgainAndSettle()
        wiring.cartLocalDataSource.stopFailingWrites()

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(colaLine(quantity = 1))
    }

    @Test
    suspend fun `orderAgain when retried after cart failure then emits navigate to cart event`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1))
        wiring.cartLocalDataSource.failWritesAfterLines(0)
        wiring.orderAgainAndSettle()
        wiring.cartLocalDataSource.stopFailingWrites()

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()

            // Then
            assertThat(awaitItem()).isEqualTo(OrdersUiEvent.NavigateToCart)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
