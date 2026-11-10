package com.example.cart.ui.cart

import app.cash.turbine.test
import com.example.auth.data.AuthTestData
import com.example.cart.data.CartTestData
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.usecase.AddCartItemUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.cart.domain.usecase.RemoveCartItemUseCase
import com.example.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.cart.ui.cart.mapper.CartDomainToUiMapper
import com.example.core.model.ProductCategory
import com.example.core.testing.MainDispatcherExtension
import com.example.core.ui.utils.formatting.DefaultCurrencyFormatter
import com.example.menu.data.MenuTestData
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.usecase.ObserveRecommendedItemsUseCase
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class CartViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val currencyFormatter = DefaultCurrencyFormatter()
    private val authTestData = AuthTestData()
    private val cartTestData = CartTestData(Clock.fixed(NOW, ZoneOffset.UTC))
    private val menuTestData = MenuTestData()

    private fun viewModel(): CartViewModel {
        val cartRepository = cartTestData.cartRepository
        val sessionRepository = authTestData.sessionRepository
        return CartViewModel(
            observeCartUseCase = ObserveCartUseCase(cartRepository, sessionRepository),
            observeRecommendedItemsUseCase = ObserveRecommendedItemsUseCase(menuTestData.recommendedItemsRepository),
            updateCartItemQuantityUseCase = UpdateCartItemQuantityUseCase(cartRepository, sessionRepository),
            removeCartItemUseCase = RemoveCartItemUseCase(cartRepository, sessionRepository),
            addCartItemUseCase = AddCartItemUseCase(cartRepository, sessionRepository),
            cartMapper = CartDomainToUiMapper(currencyFormatter),
        )
    }

    @Nested
    inner class UiState {

        @Test
        fun `uiState when the cart has lines then shows them with their prices and total`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu()
            cartTestData.givenCart(CartOwner.Guest, cola(quantity = COLA_QUANTITY))
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(CartUiState.Loading)

                // Then
                val state = awaitItem()
                assertThat(state).isInstanceOf(CartUiState.Success::class.java)
                val cart = (state as CartUiState.Success).cart
                assertThat(cart.items).singleElement().satisfies({ line ->
                    assertThat(line.name).isEqualTo(COLA_NAME)
                    assertThat(line.quantity).isEqualTo(COLA_QUANTITY)
                    assertThat(line.unitPriceFormatted).isEqualTo(currencyFormatter.format(COLA_PRICE))
                    assertThat(line.lineTotalFormatted).isEqualTo(currencyFormatter.format(COLA_LINE_TOTAL))
                })
                assertThat(cart.totalPriceFormatted).isEqualTo(currencyFormatter.format(COLA_LINE_TOTAL))
            }
        }

        @Test
        fun `uiState when the menu has a drink then shows it as a recommendation`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu(items = listOf(sprite()))
            cartTestData.givenCart(CartOwner.Guest, cola(quantity = COLA_QUANTITY))
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(CartUiState.Loading)

                // Then
                val state = awaitItem()
                assertThat(state).isInstanceOf(CartUiState.Success::class.java)
                assertThat((state as CartUiState.Success).recommendedItems).singleElement().satisfies({ item ->
                    assertThat(item.name).isEqualTo(SPRITE_NAME)
                    assertThat(item.unitPriceFormatted).isEqualTo(currencyFormatter.format(SPRITE_PRICE))
                })
            }
        }

        @Test
        fun `uiState when the cart is empty then shows the empty state`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu()
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(CartUiState.Loading)

                // Then
                assertThat(awaitItem()).isEqualTo(CartUiState.Empty)
            }
        }
    }

    private fun cola(quantity: Int) = CartItem.Other(
        lineId = COLA_LINE_ID,
        productId = COLA_ID,
        name = COLA_NAME,
        imageUrl = "",
        quantity = quantity,
        category = ProductCategory.DRINK,
        unitPrice = COLA_PRICE,
    )

    private fun sprite() = MenuItem.OtherMenuItem(
        id = SPRITE_ID,
        name = SPRITE_NAME,
        imageUrl = "",
        unitPrice = SPRITE_PRICE,
        category = ProductCategory.DRINK,
    )

    private companion object {
        const val COLA_LINE_ID = "line-cola"
        const val COLA_ID = "cola"
        const val COLA_NAME = "Cola"
        const val COLA_PRICE = 1.99
        const val COLA_QUANTITY = 2
        const val COLA_LINE_TOTAL = COLA_PRICE * COLA_QUANTITY
        const val SPRITE_ID = "sprite"
        const val SPRITE_NAME = "Sprite"
        const val SPRITE_PRICE = 2.49
        val NOW: Instant = Instant.parse("2026-11-10T12:00:00Z")
    }
}
