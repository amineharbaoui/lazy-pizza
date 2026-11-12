package com.example.menu.ui.home

import app.cash.turbine.test
import com.example.auth.data.AuthTestData
import com.example.auth.domain.usecase.ObserveIsSignedInUseCase
import com.example.auth.domain.usecase.SignOutUseCase
import com.example.cart.data.CartTestData
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.usecase.AddCartItemUseCase
import com.example.cart.domain.usecase.ClearCartUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.cart.domain.usecase.RemoveCartItemUseCase
import com.example.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.core.model.ProductCategory
import com.example.core.testing.MainDispatcherExtension
import com.example.core.ui.utils.formatting.DefaultCurrencyFormatter
import com.example.menu.data.MenuTestData
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.usecase.ObserveMenuUseCase
import com.example.menu.ui.home.mapper.MenuDomainToUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val currencyFormatter = DefaultCurrencyFormatter()
    private val authTestData = AuthTestData()
    private val cartTestData = CartTestData(Clock.fixed(NOW, ZoneOffset.UTC))
    private val menuTestData = MenuTestData()

    private fun viewModel(): MenuViewModel {
        val cartRepository = cartTestData.cartRepository
        val sessionRepository = authTestData.sessionRepository
        return MenuViewModel(
            observeMenuUseCase = ObserveMenuUseCase(menuTestData.menuRepository),
            observeCartUseCase = ObserveCartUseCase(cartRepository, sessionRepository),
            addCartItemUseCase = AddCartItemUseCase(cartRepository, sessionRepository),
            updateCartItemQuantityUseCase = UpdateCartItemQuantityUseCase(cartRepository, sessionRepository),
            removeCartItemUseCase = RemoveCartItemUseCase(cartRepository, sessionRepository),
            clearCartUseCase = ClearCartUseCase(cartRepository, sessionRepository),
            signOutUseCase = SignOutUseCase(authTestData.phoneAuthRepository),
            observeIsSignedInUseCase = ObserveIsSignedInUseCase(sessionRepository),
            menuMapper = MenuDomainToUiMapper(currencyFormatter),
        )
    }

    @Nested
    inner class UiState {

        @Test
        fun `uiState when the menu loads then shows its sections with their prices`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu(items = listOf(margherita(), cola()))
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem().content).isEqualTo(MenuContentUiState.Loading)

                // Then
                val content = awaitItem().content
                assertThat(content).isInstanceOf(MenuContentUiState.Ready::class.java)
                val menu = (content as MenuContentUiState.Ready).filteredMenu
                assertThat(menu.map { it.category })
                    .containsExactly(ProductCategory.PIZZA, ProductCategory.DRINK, ProductCategory.SAUCE, ProductCategory.ICE_CREAM)
                assertThat(menu.flatMap { it.items })
                    .extracting(MenuItemDisplayModel::name, MenuItemDisplayModel::unitPriceFormatted)
                    .containsExactly(
                        tuple(MARGHERITA_NAME, currencyFormatter.format(MARGHERITA_PRICE)),
                        tuple(COLA_NAME, currencyFormatter.format(COLA_PRICE)),
                    )
            }
        }
    }

    @Nested
    inner class UpdateSearchQuery {

        @Test
        fun `updateSearchQuery when an item matches then shows only the matching items`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu(items = listOf(margherita(), cola()))
            val viewModel = viewModel()

            viewModel.uiState.test {
                skipItems(1)
                awaitItem()

                // When
                viewModel.updateSearchQuery(MATCHING_QUERY)

                // Then
                val content = awaitItem().content as MenuContentUiState.Ready
                assertThat(content.searchQuery).isEqualTo(MATCHING_QUERY)
                assertThat(content.filteredMenu).singleElement().satisfies({ section ->
                    assertThat(section.category).isEqualTo(ProductCategory.PIZZA)
                    assertThat(section.items.map { it.name }).containsExactly(MARGHERITA_NAME)
                })
            }
        }

        @Test
        fun `updateSearchQuery when nothing matches then shows an empty menu`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu(items = listOf(margherita(), cola()))
            val viewModel = viewModel()

            viewModel.uiState.test {
                skipItems(1)
                awaitItem()

                // When
                viewModel.updateSearchQuery(UNMATCHED_QUERY)

                // Then
                val content = awaitItem().content as MenuContentUiState.Ready
                assertThat(content.searchQuery).isEqualTo(UNMATCHED_QUERY)
                assertThat(content.filteredMenu).isEmpty()
            }
        }
    }

    @Nested
    inner class AddOtherItemToCart {

        @Test
        fun `addOtherItemToCart when the item is not in the cart then shows a quantity of one`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu(items = listOf(cola()))
            val viewModel = viewModel()

            viewModel.uiState.test {
                skipItems(1)
                val colaItem = awaitItem().otherItem(COLA_ID)

                // When
                viewModel.addOtherItemToCart(colaItem)
                advanceUntilIdle()

                // Then
                assertThat(expectMostRecentItem().otherItem(COLA_ID).quantity).isEqualTo(1)
            }
        }
    }

    @Nested
    inner class UpdateOtherItemQuantity {

        @Test
        fun `updateOtherItemQuantity when the quantity changes then shows the new quantity`() = runTest {
            // Given
            authTestData.givenSignedOut()
            menuTestData.givenMenu(items = listOf(cola()))
            cartTestData.givenCart(CartOwner.Guest, colaLine(quantity = COLA_QUANTITY))
            val viewModel = viewModel()

            viewModel.uiState.test {
                skipItems(1)
                val colaItem = awaitItem().otherItem(COLA_ID)

                // When
                viewModel.updateOtherItemQuantity(colaItem, newQuantity = NEW_COLA_QUANTITY)
                advanceUntilIdle()

                // Then
                assertThat(expectMostRecentItem().otherItem(COLA_ID).quantity).isEqualTo(NEW_COLA_QUANTITY)
            }
        }
    }

    @Nested
    inner class SignOut {

        @Test
        fun `signOut when signed in then empties the cart and shows the signed out state`() = runTest {
            // Given
            authTestData.givenSignedIn(USER_ID)
            menuTestData.givenMenu(items = listOf(cola()))
            cartTestData.givenCart(CartOwner.User(USER_ID), colaLine(quantity = COLA_QUANTITY))
            val viewModel = viewModel()

            viewModel.uiState.test {
                skipItems(1)
                awaitItem()

                // When
                viewModel.signOut()
                advanceUntilIdle()

                // Then
                val state = expectMostRecentItem()
                assertThat(state.isLoggedIn).isFalse()
                assertThat(state.otherItem(COLA_ID).quantity).isZero()
                assertThat(cartTestData.cartLines(CartOwner.User(USER_ID))).isEmpty()
            }
        }
    }

    private fun MenuUiState.otherItem(id: String): MenuItemDisplayModel.Other = (content as MenuContentUiState.Ready).filteredMenu
        .flatMap { it.items }
        .filterIsInstance<MenuItemDisplayModel.Other>()
        .single { it.id == id }

    private fun margherita() = MenuItem.PizzaItem(
        id = MARGHERITA_ID,
        name = MARGHERITA_NAME,
        imageUrl = "",
        unitPrice = MARGHERITA_PRICE,
        category = ProductCategory.PIZZA,
        description = MARGHERITA_DESCRIPTION,
    )

    private fun cola() = MenuItem.OtherMenuItem(
        id = COLA_ID,
        name = COLA_NAME,
        imageUrl = "",
        unitPrice = COLA_PRICE,
        category = ProductCategory.DRINK,
    )

    private fun colaLine(quantity: Int) = CartItem.Other(
        lineId = COLA_ID,
        productId = COLA_ID,
        name = COLA_NAME,
        imageUrl = "",
        unitPrice = COLA_PRICE,
        quantity = quantity,
        category = ProductCategory.DRINK,
    )

    private companion object {
        const val USER_ID = "user-1"
        const val MARGHERITA_ID = "margherita"
        const val MARGHERITA_NAME = "Margherita"
        const val MARGHERITA_DESCRIPTION = "Tomato sauce, mozzarella"
        const val MARGHERITA_PRICE = 8.99
        const val COLA_ID = "cola"
        const val COLA_NAME = "Cola"
        const val COLA_PRICE = 1.99
        const val COLA_QUANTITY = 2
        const val NEW_COLA_QUANTITY = 3
        const val MATCHING_QUERY = "marg"
        const val UNMATCHED_QUERY = "sushi"
        val NOW: Instant = Instant.parse("2026-11-12T12:00:00Z")
    }
}
