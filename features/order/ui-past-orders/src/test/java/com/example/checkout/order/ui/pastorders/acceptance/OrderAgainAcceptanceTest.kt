package com.example.checkout.order.ui.pastorders.acceptance

import app.cash.turbine.test
import com.example.checkout.order.ui.pastorders.OrderAgainUiState
import com.example.checkout.order.ui.pastorders.OrdersUiEvent
import com.example.core.testing.MainDispatcherExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

// ORDER-03 to ORDER-08: Order again adds available items to the cart at today's prices.
class OrderAgainAcceptanceTest {

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

    // ORDER-03

    @Test
    suspend fun `orderAgain when whole order is on the menu then cart holds every item with its quantities and toppings`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(2, pastExtraCheese(1)), pastCola(1))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactlyInAnyOrder(
            margheritaLine(quantity = 2, toppingQuantities = mapOf(EXTRA_CHEESE_ID to 1)),
            colaLine(quantity = 1),
        )
    }

    @Test
    suspend fun `orderAgain when whole order is on the menu then emits navigate to cart event`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(2, pastExtraCheese(1)), pastCola(1))

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()

            // Then
            assertThat(awaitItem()).isEqualTo(OrdersUiEvent.NavigateToCart)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `orderAgain when whole order is on the menu then shows no dialog`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(2, pastExtraCheese(1)), pastCola(1))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.Idle)
    }

    // ORDER-04

    @Test
    suspend fun `orderAgain when cart already holds other items then keeps them alongside the reordered items`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1), cart = listOf(cartPepperoni()))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactlyInAnyOrder(pepperoniLine(), colaLine(quantity = 1))
    }

    // ORDER-05

    @Test
    suspend fun `orderAgain when same drink is already in the cart then updates its quantity on a single line`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1), cart = listOf(cartCola(quantity = 2)))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(colaLine(quantity = 3))
    }

    // ORDER-06

    @Test
    suspend fun `orderAgain when same pizza is already in the cart then adds a separate pizza line`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(1), cart = listOf(cartMargherita()))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(margheritaLine(quantity = 1), margheritaLine(quantity = 1))
    }

    // ORDER-07

    @Test
    suspend fun `orderAgain when menu prices changed then cart line uses todays unit and topping prices`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(2, pastExtraCheese(2)))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartItems().single().lineTotal).isEqualTo(EXPECTED_MARGHERITA_LINE_TOTAL_TODAY)
    }

    // ORDER-08

    @Test
    fun `orderAgain when tapped while a reorder is in progress then shows every order again button disabled`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1))
        wiring.cartLocalDataSource.holdAddItems()
        wiring.orderAgainAndSettle()

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.InProgress)
    }

    @Test
    fun `orderAgain when in progress reorder finishes then shows order again buttons enabled`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1))
        wiring.cartLocalDataSource.holdAddItems()
        wiring.orderAgainAndSettle()

        // When
        wiring.cartLocalDataSource.releaseAddItems()
        wiring.settle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.Idle)
    }

    @Test
    suspend fun `orderAgain when tapped twice before the first finishes then cart holds the order once`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1))
        wiring.cartLocalDataSource.holdAddItems()

        // When
        wiring.orderAgainAndSettle()
        wiring.orderAgainAndSettle()
        wiring.cartLocalDataSource.releaseAddItems()
        wiring.settle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(colaLine(quantity = 1))
    }

    @Test
    suspend fun `orderAgain when tapped twice before the first finishes then emits navigate to cart once`() {
        // Given
        wiring.openOrdersScreenWith(pastCola(1))
        wiring.cartLocalDataSource.holdAddItems()

        wiring.viewModel.events.test {
            // When
            wiring.orderAgainAndSettle()
            wiring.orderAgainAndSettle()
            wiring.cartLocalDataSource.releaseAddItems()
            wiring.settle()

            // Then
            assertThat(awaitItem()).isEqualTo(OrdersUiEvent.NavigateToCart)
            expectNoEvents()
        }
    }
}
