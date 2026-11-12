package com.example.menu.ui.pizzadetail

import app.cash.turbine.test
import com.example.auth.data.AuthTestData
import com.example.cart.data.CartTestData
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.model.CartTopping
import com.example.cart.domain.usecase.AddCartItemUseCase
import com.example.cart.domain.usecase.ObserveCartItemUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.core.model.ProductCategory
import com.example.core.testing.MainDispatcherExtension
import com.example.core.ui.utils.formatting.DefaultCurrencyFormatter
import com.example.menu.data.MenuTestData
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.Topping
import com.example.menu.domain.usecase.ObservePizzaDetailUseCase
import com.example.menu.ui.pizzadetail.mapper.PizzaDetailDomainToUiMapper
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class PizzaDetailViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val currencyFormatter = DefaultCurrencyFormatter()
    private val authTestData = AuthTestData()
    private val cartTestData = CartTestData(Clock.fixed(NOW, ZoneOffset.UTC))
    private val menuTestData = MenuTestData()

    private fun viewModel(): PizzaDetailViewModel {
        val cartRepository = cartTestData.cartRepository
        val sessionRepository = authTestData.sessionRepository
        return PizzaDetailViewModel(
            observePizzaDetailUseCase = ObservePizzaDetailUseCase(menuTestData.menuRepository),
            observeCartItemUseCase = ObserveCartItemUseCase(ObserveCartUseCase(cartRepository, sessionRepository)),
            addCartItemUseCase = AddCartItemUseCase(cartRepository, sessionRepository),
            pizzaDetailMapper = PizzaDetailDomainToUiMapper(currencyFormatter),
        )
    }

    @Nested
    inner class Initialize {

        @Test
        fun `initialize when the pizza is on the menu then shows it with its toppings and price`() = runTest {
            // Given
            menuTestData.givenMenu(items = listOf(margherita()), toppings = listOf(cheese(), bacon()))
            val viewModel = viewModel()

            // When
            viewModel.initialize(args = PizzaDetailArgs.Create(MARGHERITA_ID), sessionId = SESSION_ID)

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PizzaDetailUiState.Loading)

                // Then
                val state = awaitItem()
                assertThat(state).isInstanceOf(PizzaDetailUiState.Ready::class.java)
                val ready = state as PizzaDetailUiState.Ready
                assertThat(ready.pizza.name).isEqualTo(MARGHERITA_NAME)
                assertThat(ready.pizza.description).isEqualTo(MARGHERITA_DESCRIPTION)
                assertThat(ready.pizza.unitPriceFormatted).isEqualTo(currencyFormatter.format(MARGHERITA_PRICE))
                assertThat(ready.toppings)
                    .extracting(ToppingDisplayModel::name, ToppingDisplayModel::unitPriceFormatted)
                    .containsExactly(
                        tuple(CHEESE_NAME, currencyFormatter.format(CHEESE_PRICE)),
                        tuple(BACON_NAME, currencyFormatter.format(BACON_PRICE)),
                    )
                assertThat(ready.toppingQuantities).containsExactlyInAnyOrderEntriesOf(
                    mapOf(CHEESE_ID to NO_TOPPING, BACON_ID to NO_TOPPING),
                )
                assertThat(ready.quantity).isEqualTo(DEFAULT_QUANTITY)
                assertThat(ready.totalPriceFormatted).isEqualTo(currencyFormatter.format(MARGHERITA_PRICE))
            }
        }

        @Test
        fun `initialize when the menu fails to load then shows the error state`() = runTest {
            // Given
            menuTestData.givenMenuFailure()
            val viewModel = viewModel()

            // When
            viewModel.initialize(args = PizzaDetailArgs.Create(MARGHERITA_ID), sessionId = SESSION_ID)

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PizzaDetailUiState.Loading)

                // Then
                assertThat(awaitItem()).isEqualTo(PizzaDetailUiState.Error)
            }
        }

        @Test
        fun `initialize when the pizza is not on the menu then shows the error state`() = runTest {
            // Given
            menuTestData.givenMenu(toppings = listOf(cheese()))
            val viewModel = viewModel()

            // When
            viewModel.initialize(args = PizzaDetailArgs.Create(MARGHERITA_ID), sessionId = SESSION_ID)

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(PizzaDetailUiState.Loading)

                // Then
                assertThat(awaitItem()).isEqualTo(PizzaDetailUiState.Error)
            }
        }
    }

    @Nested
    inner class UpdateToppingQuantity {

        @Test
        fun `updateToppingQuantity when a topping is added then updates the total`() = runTest {
            // Given
            menuTestData.givenMenu(items = listOf(margherita()), toppings = listOf(cheese(), bacon()))
            val viewModel = viewModel()
            viewModel.initialize(args = PizzaDetailArgs.Create(MARGHERITA_ID), sessionId = SESSION_ID)

            viewModel.uiState.test {
                skipItems(1)
                awaitItem()

                // When
                viewModel.updateToppingQuantity(toppingId = CHEESE_ID, newQuantity = CHEESE_QUANTITY)

                // Then
                val state = awaitItem() as PizzaDetailUiState.Ready
                assertThat(state.toppingQuantities).containsEntry(CHEESE_ID, CHEESE_QUANTITY)
                assertThat(state.totalPriceFormatted)
                    .isEqualTo(currencyFormatter.format(MARGHERITA_WITH_CHEESE_PRICE))
            }
        }

        @Test
        fun `updateToppingQuantity when a topping is removed then updates the total`() = runTest {
            // Given
            menuTestData.givenMenu(items = listOf(margherita()), toppings = listOf(cheese(), bacon()))
            val viewModel = viewModel()
            viewModel.initialize(args = PizzaDetailArgs.Create(MARGHERITA_ID), sessionId = SESSION_ID)

            viewModel.uiState.test {
                skipItems(1)
                awaitItem()
                viewModel.updateToppingQuantity(toppingId = CHEESE_ID, newQuantity = CHEESE_QUANTITY)
                awaitItem()

                // When
                viewModel.updateToppingQuantity(toppingId = CHEESE_ID, newQuantity = NO_TOPPING)

                // Then
                val state = awaitItem() as PizzaDetailUiState.Ready
                assertThat(state.toppingQuantities).containsEntry(CHEESE_ID, NO_TOPPING)
                assertThat(state.totalPriceFormatted).isEqualTo(currencyFormatter.format(MARGHERITA_PRICE))
            }
        }
    }

    @Nested
    inner class UpdateQuantity {

        @Test
        fun `updateQuantity when the quantity changes then updates the total with its toppings`() = runTest {
            // Given
            menuTestData.givenMenu(items = listOf(margherita()), toppings = listOf(cheese(), bacon()))
            val viewModel = viewModel()
            viewModel.initialize(args = PizzaDetailArgs.Create(MARGHERITA_ID), sessionId = SESSION_ID)

            viewModel.uiState.test {
                skipItems(1)
                awaitItem()
                viewModel.updateToppingQuantity(toppingId = CHEESE_ID, newQuantity = CHEESE_QUANTITY)
                awaitItem()

                // When
                viewModel.updateQuantity(PIZZA_QUANTITY)

                // Then
                val state = awaitItem() as PizzaDetailUiState.Ready
                assertThat(state.quantity).isEqualTo(PIZZA_QUANTITY)
                assertThat(state.totalPriceFormatted)
                    .isEqualTo(currencyFormatter.format(MARGHERITA_WITH_CHEESE_LINE_TOTAL))
            }
        }
    }

    @Nested
    inner class AddItemToCart {

        @Test
        fun `addItemToCart when toppings are chosen then stores the pizza with its toppings in the cart`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu(items = listOf(margherita()), toppings = listOf(cheese(), bacon()))
            val viewModel = viewModel()
            viewModel.initialize(args = PizzaDetailArgs.Create(MARGHERITA_ID), sessionId = SESSION_ID)

            viewModel.uiState.test {
                skipItems(1)
                awaitItem()
                viewModel.updateToppingQuantity(toppingId = CHEESE_ID, newQuantity = CHEESE_QUANTITY)
                awaitItem()
                viewModel.updateQuantity(PIZZA_QUANTITY)
                val chosen = awaitItem() as PizzaDetailUiState.Ready

                // When
                viewModel.addItemToCart(chosen)
            }
            advanceUntilIdle()

            // Then
            assertThat(cartTestData.cartLines(CartOwner.Guest)).singleElement()
                .usingRecursiveComparison()
                .ignoringFields("lineId")
                .isEqualTo(margheritaWithCheeseLine())
        }
    }

    private fun margherita() = MenuItem.PizzaItem(
        id = MARGHERITA_ID,
        name = MARGHERITA_NAME,
        imageUrl = MARGHERITA_IMAGE_URL,
        unitPrice = MARGHERITA_PRICE,
        category = ProductCategory.PIZZA,
        description = MARGHERITA_DESCRIPTION,
    )

    private fun cheese() = Topping(id = CHEESE_ID, name = CHEESE_NAME, unitPrice = CHEESE_PRICE, imageUrl = "")

    private fun bacon() = Topping(id = BACON_ID, name = BACON_NAME, unitPrice = BACON_PRICE, imageUrl = "")

    private fun margheritaWithCheeseLine() = CartItem.Pizza(
        lineId = "",
        productId = MARGHERITA_ID,
        name = MARGHERITA_NAME,
        imageUrl = MARGHERITA_IMAGE_URL,
        quantity = PIZZA_QUANTITY,
        category = ProductCategory.PIZZA,
        unitPrice = MARGHERITA_PRICE,
        toppings = listOf(CartTopping(id = CHEESE_ID, name = CHEESE_NAME, unitPrice = CHEESE_PRICE, quantity = CHEESE_QUANTITY)),
    )

    private companion object {
        const val SESSION_ID = "session"
        const val MARGHERITA_ID = "margherita"
        const val MARGHERITA_NAME = "Margherita"
        const val MARGHERITA_DESCRIPTION = "Tomato sauce, mozzarella, fresh basil"
        const val MARGHERITA_IMAGE_URL = "https://example.com/margherita.png"
        const val MARGHERITA_PRICE = 8.99
        const val CHEESE_ID = "extra-cheese"
        const val CHEESE_NAME = "Extra cheese"
        const val CHEESE_PRICE = 1.00
        const val BACON_ID = "bacon"
        const val BACON_NAME = "Bacon"
        const val BACON_PRICE = 1.50
        const val NO_TOPPING = 0
        const val CHEESE_QUANTITY = 2
        const val DEFAULT_QUANTITY = 1
        const val PIZZA_QUANTITY = 2
        const val MARGHERITA_WITH_CHEESE_PRICE = MARGHERITA_PRICE + CHEESE_PRICE * CHEESE_QUANTITY
        const val MARGHERITA_WITH_CHEESE_LINE_TOTAL = MARGHERITA_WITH_CHEESE_PRICE * PIZZA_QUANTITY
        val NOW: Instant = Instant.parse("2026-11-10T12:00:00Z")
    }
}
