package com.example.checkout.order.ui.pastorders.acceptance

import app.cash.turbine.Event
import app.cash.turbine.test
import com.example.checkout.order.ui.pastorders.OrderAgainUiState
import com.example.checkout.order.ui.pastorders.OrdersUiEvent
import com.example.checkout.order.ui.pastorders.SkippedItemUi
import com.example.core.testing.MainDispatcherExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

// ORDER-09 to ORDER-14: Order again skips what is no longer on the menu and tells the user.
class OrderAgainSkippedItemsAcceptanceTest {

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

    /** ORDER-11 / ORDER-12 Given: 1 x Margherita and 1 x Lemonade reordered, the dialog is showing. */
    private fun givenSomeSkippedDialogShowing() {
        wiring.openOrdersScreenWith(pastMargherita(1), pastLemonade(1))
        wiring.orderAgainAndSettle()
    }

    /** ORDER-14 Given: 1 x Lemonade reordered, the nothing available dialog is showing. */
    private fun givenNothingAvailableDialogShowing() {
        wiring.openOrdersScreenWith(pastLemonade(1), cart = listOf(cartPepperoni()))
        wiring.orderAgainAndSettle()
    }

    // ORDER-09

    @Test
    suspend fun `orderAgain when a product is off the menu then cart holds only the available items`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(1), pastLemonade(1))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(margheritaLine(quantity = 1))
    }

    @Test
    fun `orderAgain when a product is off the menu then shows some skipped dialog naming the product`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(1), pastLemonade(1))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.orderAgainState())
            .isEqualTo(OrderAgainUiState.SomeSkipped(listOf(SkippedItemUi.Product(LEMONADE_NAME))))
    }

    // ORDER-10

    @Test
    suspend fun `orderAgain when a topping is off the menu then cart holds the pizza without that topping`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(1, pastExtraCheese(1), pastTruffle(1)))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines())
            .containsExactly(margheritaLine(quantity = 1, toppingQuantities = mapOf(EXTRA_CHEESE_ID to 1)))
    }

    @Test
    fun `orderAgain when a topping is off the menu then shows some skipped dialog naming the topping on its pizza`() {
        // Given
        wiring.openOrdersScreenWith(pastMargherita(1, pastExtraCheese(1), pastTruffle(1)))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.orderAgainState())
            .isEqualTo(OrderAgainUiState.SomeSkipped(listOf(SkippedItemUi.Topping(TRUFFLE_NAME, MARGHERITA_NAME))))
    }

    // ORDER-11

    @Test
    fun `goToCartFromSkippedItems when some skipped dialog is showing then closes the dialog`() {
        // Given
        givenSomeSkippedDialogShowing()

        // When
        wiring.viewModel.goToCartFromSkippedItems()
        wiring.settle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.Idle)
    }

    @Test
    suspend fun `goToCartFromSkippedItems when some skipped dialog is showing then emits navigate to cart event`() {
        // Given
        givenSomeSkippedDialogShowing()

        wiring.viewModel.events.test {
            // When
            wiring.viewModel.goToCartFromSkippedItems()
            wiring.settle()

            // Then
            assertThat(awaitItem()).isEqualTo(OrdersUiEvent.NavigateToCart)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    suspend fun `goToCartFromSkippedItems when some skipped dialog is showing then cart holds the available items`() {
        // Given
        givenSomeSkippedDialogShowing()

        // When
        wiring.viewModel.goToCartFromSkippedItems()
        wiring.settle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(margheritaLine(quantity = 1))
    }

    // ORDER-12

    @Test
    fun `dismissOrderAgainDialog when some skipped dialog is showing then closes the dialog`() {
        // Given
        givenSomeSkippedDialogShowing()

        // When
        wiring.viewModel.dismissOrderAgainDialog()
        wiring.settle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.Idle)
    }

    @Test
    suspend fun `dismissOrderAgainDialog when some skipped dialog is showing then emits no navigation event`() {
        wiring.viewModel.events.test {
            // Given
            givenSomeSkippedDialogShowing()

            // When
            wiring.viewModel.dismissOrderAgainDialog()
            wiring.settle()

            // Then
            assertThat(cancelAndConsumeRemainingEvents()).doesNotContain(Event.Item(OrdersUiEvent.NavigateToCart))
        }
    }

    @Test
    suspend fun `dismissOrderAgainDialog when some skipped dialog is showing then cart holds the available items`() {
        // Given
        givenSomeSkippedDialogShowing()

        // When
        wiring.viewModel.dismissOrderAgainDialog()
        wiring.settle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(margheritaLine(quantity = 1))
    }

    // ORDER-13

    @Test
    fun `orderAgain when nothing in the order is on the menu then shows nothing available dialog naming the products`() {
        // Given
        wiring.openOrdersScreenWith(pastLemonade(1), cart = listOf(cartPepperoni()))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.orderAgainState())
            .isEqualTo(OrderAgainUiState.NothingAvailable(listOf(SkippedItemUi.Product(LEMONADE_NAME))))
    }

    @Test
    suspend fun `orderAgain when nothing in the order is on the menu then leaves the cart unchanged`() {
        // Given
        wiring.openOrdersScreenWith(pastLemonade(1), cart = listOf(cartPepperoni()))

        // When
        wiring.orderAgainAndSettle()

        // Then
        assertThat(wiring.cartLines()).containsExactly(pepperoniLine())
    }

    // ORDER-14

    @Test
    fun `dismissOrderAgainDialog when nothing available dialog is showing then closes the dialog`() {
        // Given
        givenNothingAvailableDialogShowing()

        // When
        wiring.viewModel.dismissOrderAgainDialog()
        wiring.settle()

        // Then
        assertThat(wiring.orderAgainState()).isEqualTo(OrderAgainUiState.Idle)
    }

    @Test
    suspend fun `dismissOrderAgainDialog when nothing available dialog is showing then emits no navigation event`() {
        wiring.viewModel.events.test {
            // Given
            givenNothingAvailableDialogShowing()

            // When
            wiring.viewModel.dismissOrderAgainDialog()
            wiring.settle()

            // Then
            assertThat(cancelAndConsumeRemainingEvents()).doesNotContain(Event.Item(OrdersUiEvent.NavigateToCart))
        }
    }
}
