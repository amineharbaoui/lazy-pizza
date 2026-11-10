package com.example.order.ui.pastorders

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.example.auth.data.AuthTestData
import com.example.auth.domain.usecase.ObserveCurrentUserUidUseCase
import com.example.cart.data.CartTestData
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.model.CartTopping
import com.example.cart.domain.model.UnavailableItem
import com.example.cart.domain.usecase.MergeItemsIntoCartUseCase
import com.example.core.model.OrderStatus
import com.example.core.model.ProductCategory
import com.example.core.testing.MainDispatcherExtension
import com.example.core.ui.utils.formatting.DefaultCurrencyFormatter
import com.example.menu.data.MenuTestData
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.Topping
import com.example.menu.domain.usecase.GetCurrentMenuUseCase
import com.example.order.data.OrderTestData
import com.example.order.domain.model.Order
import com.example.order.domain.model.OrderItem
import com.example.order.domain.model.OrderTopping
import com.example.order.domain.model.PickupType
import com.example.order.domain.usecase.ObserveOrdersUseCase
import com.example.order.domain.usecase.ReorderUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val clock = Clock.fixed(NOW, ZoneOffset.UTC)
    private val authTestData = AuthTestData()
    private val orderTestData = OrderTestData()
    private val cartTestData = CartTestData(clock)
    private val menuTestData = MenuTestData()

    private fun viewModel() = OrdersViewModel(
        observeCurrentUserUidUseCase = ObserveCurrentUserUidUseCase(authTestData.sessionRepository),
        observeOrdersUseCase = ObserveOrdersUseCase(orderTestData.ordersRepository, clock),
        orderToUiMapper = OrderToUiMapper(DefaultCurrencyFormatter()),
        reorderUseCase = ReorderUseCase(
            getCurrentMenuUseCase = GetCurrentMenuUseCase(menuTestData.menuRepository),
            mergeItemsIntoCartUseCase = MergeItemsIntoCartUseCase(cartTestData.cartRepository, authTestData.sessionRepository),
            unavailableItemsRepository = cartTestData.unavailableItemsRepository,
        ),
    )

    private fun cartLines() = cartTestData.cartLines(CartOwner.User(USER_ID))

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

    @Nested
    inner class Reorder {

        @Test
        fun `reorder when every item is on the menu then adds the order to the cart and navigates to the cart`() = runTest {
            // Given
            givenSignedInWithOrder(
                margheritaOrderItem(toppings = listOf(extraCheeseOrderTopping(), pepperoniOrderTopping(quantity = PEPPERONI_QUANTITY))),
                colaOrderItem(quantity = COLA_ORDER_QUANTITY),
            )
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                val state = expectMostRecentItem() as OrderUiState.Ready
                assertThat(state.navigation).isEqualTo(OrdersNavigation.CART)
                assertThat(state.dialog).isNull()
                assertThat(cartLines()).satisfiesExactly(
                    { line ->
                        assertThat(line).isInstanceOf(CartItem.Pizza::class.java)
                        assertThat(line.productId).isEqualTo(MARGHERITA_ID)
                        assertThat(line.quantity).isEqualTo(SINGLE_QUANTITY)
                        assertThat((line as CartItem.Pizza).toppings.map { it.id to it.quantity })
                            .containsExactlyInAnyOrder(EXTRA_CHEESE_ID to SINGLE_QUANTITY, PEPPERONI_TOPPING_ID to PEPPERONI_QUANTITY)
                    },
                    { line ->
                        assertThat(line.productId).isEqualTo(COLA_ID)
                        assertThat(line.quantity).isEqualTo(COLA_ORDER_QUANTITY)
                    },
                )
            }
        }

        @Test
        fun `reorder when every item is on the menu then leaves no unavailable items`() = runTest {
            // Given
            givenSignedInWithOrder(colaOrderItem())
            cartTestData.givenUnavailableItems(UnavailableItem.Item(HAWAIIAN_NAME))
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(unavailableItems()).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when the cart has other items then keeps them`() = runTest {
            // Given
            givenSignedInWithOrder(margheritaOrderItem())
            cartTestData.givenCart(CartOwner.User(USER_ID), pepperoniPizzaLine())
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines().map { it.productId }).containsExactly(PEPPERONI_PIZZA_ID, MARGHERITA_ID)
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when prices changed then adds the items at the current menu prices`() = runTest {
            // Given
            givenSignedInWithOrder(
                margheritaOrderItem(
                    unitPrice = MARGHERITA_OLD_PRICE,
                    toppings = listOf(extraCheeseOrderTopping(unitPrice = EXTRA_CHEESE_OLD_PRICE)),
                ),
            )
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines()).singleElement().satisfies({ line ->
                    assertThat((line as CartItem.Pizza).unitPrice).isEqualTo(MARGHERITA_PRICE)
                    assertThat(line.lineTotal).isCloseTo(MARGHERITA_WITH_EXTRA_CHEESE_TOTAL, within(PRICE_TOLERANCE))
                })
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when the cart has the same drink then grows its quantity`() = runTest {
            // Given
            givenSignedInWithOrder(colaOrderItem(quantity = COLA_ORDER_QUANTITY))
            cartTestData.givenCart(CartOwner.User(USER_ID), colaLine(quantity = CART_COLA_QUANTITY))
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines()).singleElement().satisfies({ line ->
                    assertThat(line.lineId).isEqualTo(COLA_LINE_ID)
                    assertThat(line.quantity).isEqualTo(CART_COLA_QUANTITY + COLA_ORDER_QUANTITY)
                })
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when the cart has the same drink then the line takes the current menu price`() = runTest {
            // Given
            givenSignedInWithOrder(colaOrderItem(quantity = COLA_ORDER_QUANTITY))
            cartTestData.givenCart(CartOwner.User(USER_ID), colaLine(quantity = CART_COLA_QUANTITY, unitPrice = COLA_OLD_PRICE))
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines()).singleElement().satisfies({ line ->
                    assertThat((line as CartItem.Other).unitPrice).isEqualTo(COLA_PRICE)
                    assertThat(line.lineTotal).isCloseTo(THREE_COLAS_TOTAL, within(PRICE_TOLERANCE))
                })
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when the cart has identical pizza lines then grows the first one`() = runTest {
            // Given
            givenSignedInWithOrder(
                margheritaOrderItem(toppings = listOf(extraCheeseOrderTopping())),
                margheritaOrderItem(),
            )
            cartTestData.givenCart(
                CartOwner.User(USER_ID),
                margheritaLine(lineId = FIRST_LINE_ID, toppings = listOf(extraCheeseCartTopping())),
                margheritaLine(lineId = SECOND_LINE_ID, toppings = listOf(extraCheeseCartTopping())),
            )
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines()).satisfiesExactly(
                    { line ->
                        assertThat(line.lineId).isEqualTo(FIRST_LINE_ID)
                        assertThat(line.quantity).isEqualTo(MERGED_QUANTITY)
                    },
                    { line ->
                        assertThat(line.lineId).isEqualTo(SECOND_LINE_ID)
                        assertThat(line.quantity).isEqualTo(SINGLE_QUANTITY)
                    },
                    { line ->
                        assertThat(line.productId).isEqualTo(MARGHERITA_ID)
                        assertThat(line.quantity).isEqualTo(SINGLE_QUANTITY)
                        assertThat((line as CartItem.Pizza).toppings).isEmpty()
                    },
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when the cart has the same pizza with its toppings in another order then grows it`() = runTest {
            // Given
            givenSignedInWithOrder(
                margheritaOrderItem(toppings = listOf(extraCheeseOrderTopping(), pepperoniOrderTopping())),
            )
            cartTestData.givenCart(
                CartOwner.User(USER_ID),
                margheritaLine(lineId = FIRST_LINE_ID, toppings = listOf(pepperoniCartTopping(), extraCheeseCartTopping())),
            )
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines()).singleElement().satisfies({ line ->
                    assertThat(line.lineId).isEqualTo(FIRST_LINE_ID)
                    assertThat(line.quantity).isEqualTo(MERGED_QUANTITY)
                })
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when an item is no longer on the menu then adds the others`() = runTest {
            // Given
            givenSignedInWithOrder(margheritaOrderItem(), hawaiianOrderItem())
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat((expectMostRecentItem() as OrderUiState.Ready).navigation).isEqualTo(OrdersNavigation.CART)
                assertThat(cartLines().map { it.productId }).containsExactly(MARGHERITA_ID)
            }
        }

        @Test
        fun `reorder when an item is no longer on the menu then saves it as unavailable`() = runTest {
            // Given
            givenSignedInWithOrder(margheritaOrderItem(), hawaiianOrderItem())
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(unavailableItems()).containsExactly(UnavailableItem.Item(HAWAIIAN_NAME))
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when a topping is no longer offered then merges the pizza without it`() = runTest {
            // Given
            givenSignedInWithOrder(margheritaOrderItem(toppings = listOf(extraBaconOrderTopping())))
            cartTestData.givenCart(CartOwner.User(USER_ID), margheritaLine(lineId = FIRST_LINE_ID))
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines()).singleElement().satisfies({ line ->
                    assertThat(line.lineId).isEqualTo(FIRST_LINE_ID)
                    assertThat(line.quantity).isEqualTo(MERGED_QUANTITY)
                    assertThat((line as CartItem.Pizza).toppings).isEmpty()
                })
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when a topping is no longer offered then saves it as unavailable`() = runTest {
            // Given
            givenSignedInWithOrder(margheritaOrderItem(toppings = listOf(extraBaconOrderTopping())))
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(unavailableItems())
                    .containsExactly(UnavailableItem.Topping(name = EXTRA_BACON_NAME, pizzaName = MARGHERITA_NAME))
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when order lines become identical then adds them as one line`() = runTest {
            // Given
            givenSignedInWithOrder(
                margheritaOrderItem(toppings = listOf(extraBaconOrderTopping())),
                margheritaOrderItem(),
            )
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines()).singleElement().satisfies({ line ->
                    assertThat(line.productId).isEqualTo(MARGHERITA_ID)
                    assertThat(line.quantity).isEqualTo(MERGED_QUANTITY)
                    assertThat((line as CartItem.Pizza).toppings).isEmpty()
                })
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when order lines become identical then saves the topping as unavailable`() = runTest {
            // Given
            givenSignedInWithOrder(
                margheritaOrderItem(toppings = listOf(extraBaconOrderTopping())),
                margheritaOrderItem(),
            )
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(unavailableItems())
                    .containsExactly(UnavailableItem.Topping(name = EXTRA_BACON_NAME, pizzaName = MARGHERITA_NAME))
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `reorder when nothing is on the menu then shows the cannot reorder dialog and leaves the cart unchanged`() = runTest {
            // Given
            givenSignedInWithOrder(hawaiianOrderItem())
            cartTestData.givenCart(CartOwner.User(USER_ID), colaLine(quantity = CART_COLA_QUANTITY))
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                val state = expectMostRecentItem() as OrderUiState.Ready
                assertThat(state.dialog).isEqualTo(ReorderDialog.NOTHING_AVAILABLE)
                assertThat(state.navigation).isNull()
                assertThat(state.isReordering).isFalse()
                assertThat(cartLines()).containsExactly(colaLine(quantity = CART_COLA_QUANTITY))
            }
        }

        @Test
        fun `reorder when the menu cannot be loaded then shows the could not reorder dialog and leaves the cart unchanged`() = runTest {
            // Given
            givenSignedInWithOrder(colaOrderItem())
            cartTestData.givenCart(CartOwner.User(USER_ID), pepperoniPizzaLine())
            menuTestData.givenMenuUnavailable()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                val state = expectMostRecentItem() as OrderUiState.Ready
                assertThat(state.dialog).isEqualTo(ReorderDialog.FAILED)
                assertThat(state.navigation).isNull()
                assertThat(state.isReordering).isFalse()
                assertThat(cartLines()).containsExactly(pepperoniPizzaLine())
            }
        }

        @Test
        fun `reorder when tapped twice then adds the order once`() = runTest {
            // Given
            givenSignedInWithOrder(colaOrderItem(quantity = COLA_ORDER_QUANTITY))
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.reorder(ORDER_NUMBER)
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()

                // Then
                assertThat(cartLines()).singleElement().satisfies({ line ->
                    assertThat(line.productId).isEqualTo(COLA_ID)
                    assertThat(line.quantity).isEqualTo(COLA_ORDER_QUANTITY)
                })
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    inner class DismissDialog {

        @Test
        fun `dismissDialog then hides the dialog`() = runTest {
            // Given
            givenSignedInWithOrder(hawaiianOrderItem())
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()
                assertThat((expectMostRecentItem() as OrderUiState.Ready).dialog).isEqualTo(ReorderDialog.NOTHING_AVAILABLE)

                // When
                viewModel.dismissDialog()
                advanceUntilIdle()

                // Then
                assertThat((expectMostRecentItem() as OrderUiState.Ready).dialog).isNull()
            }
        }
    }

    @Nested
    inner class NavigationHandled {

        @Test
        fun `navigationHandled then clears the navigation`() = runTest {
            // Given
            givenSignedInWithOrder(colaOrderItem())
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()
                viewModel.reorder(ORDER_NUMBER)
                advanceUntilIdle()
                assertThat((expectMostRecentItem() as OrderUiState.Ready).navigation).isEqualTo(OrdersNavigation.CART)

                // When
                viewModel.navigationHandled()
                advanceUntilIdle()

                // Then
                assertThat((expectMostRecentItem() as OrderUiState.Ready).navigation).isNull()
            }
        }
    }

    /** A signed-in user with one order of these items, and today's menu. */
    private fun givenSignedInWithOrder(vararg items: OrderItem) {
        authTestData.givenSignedIn(USER_ID)
        orderTestData.givenOrders(order(userId = USER_ID, items = items.toList()))
        menuTestData.givenMenu(
            items = listOf(margherita(), pepperoniPizza(), cola()),
            toppings = listOf(extraCheese(), pepperoniTopping()),
        )
    }

    private suspend fun ReceiveTurbine<OrderUiState>.awaitReady() {
        assertThat(awaitItem()).isEqualTo(OrderUiState.Loading)
        assertThat(awaitItem()).isInstanceOf(OrderUiState.Ready::class.java)
    }

    private suspend fun unavailableItems() = cartTestData.unavailableItemsRepository.observeUnavailableItems().first()

    private fun order(
        userId: String,
        orderNumber: String = ORDER_NUMBER,
        items: List<OrderItem> = listOf(colaOrderItem()),
    ) = Order(
        orderNumber = orderNumber,
        userId = userId,
        createdAt = NOW,
        pickupType = PickupType.ASAP,
        pickupAt = NOW,
        status = OrderStatus.COMPLETED,
        comment = "",
        items = items,
        total = COLA_OLD_PRICE,
    )

    private fun margheritaOrderItem(
        unitPrice: Double = MARGHERITA_PRICE,
        toppings: List<OrderTopping> = emptyList(),
    ) = OrderItem(
        productId = MARGHERITA_ID,
        name = MARGHERITA_NAME,
        unitPrice = unitPrice,
        quantity = SINGLE_QUANTITY,
        category = ProductCategory.PIZZA,
        toppings = toppings,
    )

    private fun hawaiianOrderItem() = OrderItem(
        productId = HAWAIIAN_ID,
        name = HAWAIIAN_NAME,
        unitPrice = MARGHERITA_PRICE,
        quantity = SINGLE_QUANTITY,
        category = ProductCategory.PIZZA,
    )

    private fun colaOrderItem(quantity: Int = SINGLE_QUANTITY) = OrderItem(
        productId = COLA_ID,
        name = COLA_NAME,
        unitPrice = COLA_OLD_PRICE,
        quantity = quantity,
        category = ProductCategory.DRINK,
    )

    private fun extraCheeseOrderTopping(unitPrice: Double = EXTRA_CHEESE_PRICE) = OrderTopping(
        id = EXTRA_CHEESE_ID,
        name = EXTRA_CHEESE_NAME,
        unitPrice = unitPrice,
        quantity = SINGLE_QUANTITY,
    )

    private fun pepperoniOrderTopping(quantity: Int = SINGLE_QUANTITY) = OrderTopping(
        id = PEPPERONI_TOPPING_ID,
        name = PEPPERONI_NAME,
        unitPrice = PEPPERONI_TOPPING_PRICE,
        quantity = quantity,
    )

    private fun extraBaconOrderTopping() = OrderTopping(
        id = EXTRA_BACON_ID,
        name = EXTRA_BACON_NAME,
        unitPrice = EXTRA_CHEESE_PRICE,
        quantity = SINGLE_QUANTITY,
    )

    private fun margherita() = MenuItem.PizzaItem(
        id = MARGHERITA_ID,
        name = MARGHERITA_NAME,
        imageUrl = "",
        unitPrice = MARGHERITA_PRICE,
        category = ProductCategory.PIZZA,
        description = "",
    )

    private fun pepperoniPizza() = MenuItem.PizzaItem(
        id = PEPPERONI_PIZZA_ID,
        name = PEPPERONI_NAME,
        imageUrl = "",
        unitPrice = MARGHERITA_PRICE,
        category = ProductCategory.PIZZA,
        description = "",
    )

    private fun cola() = MenuItem.OtherMenuItem(
        id = COLA_ID,
        name = COLA_NAME,
        imageUrl = "",
        unitPrice = COLA_PRICE,
        category = ProductCategory.DRINK,
    )

    private fun extraCheese() = Topping(id = EXTRA_CHEESE_ID, name = EXTRA_CHEESE_NAME, unitPrice = EXTRA_CHEESE_PRICE, imageUrl = "")

    private fun pepperoniTopping() = Topping(
        id = PEPPERONI_TOPPING_ID,
        name = PEPPERONI_NAME,
        unitPrice = PEPPERONI_TOPPING_PRICE,
        imageUrl = "",
    )

    private fun margheritaLine(
        lineId: String,
        toppings: List<CartTopping> = emptyList(),
    ) = CartItem.Pizza(
        lineId = lineId,
        productId = MARGHERITA_ID,
        name = MARGHERITA_NAME,
        imageUrl = "",
        quantity = SINGLE_QUANTITY,
        category = ProductCategory.PIZZA,
        unitPrice = MARGHERITA_PRICE,
        toppings = toppings,
    )

    private fun pepperoniPizzaLine() = CartItem.Pizza(
        lineId = PEPPERONI_PIZZA_LINE_ID,
        productId = PEPPERONI_PIZZA_ID,
        name = PEPPERONI_NAME,
        imageUrl = "",
        quantity = SINGLE_QUANTITY,
        category = ProductCategory.PIZZA,
        unitPrice = MARGHERITA_PRICE,
        toppings = emptyList(),
    )

    private fun colaLine(
        quantity: Int,
        unitPrice: Double = COLA_PRICE,
    ) = CartItem.Other(
        lineId = COLA_LINE_ID,
        productId = COLA_ID,
        name = COLA_NAME,
        imageUrl = "",
        quantity = quantity,
        category = ProductCategory.DRINK,
        unitPrice = unitPrice,
    )

    private fun extraCheeseCartTopping() = CartTopping(
        id = EXTRA_CHEESE_ID,
        name = EXTRA_CHEESE_NAME,
        unitPrice = EXTRA_CHEESE_PRICE,
        quantity = SINGLE_QUANTITY,
    )

    private fun pepperoniCartTopping() = CartTopping(
        id = PEPPERONI_TOPPING_ID,
        name = PEPPERONI_NAME,
        unitPrice = PEPPERONI_TOPPING_PRICE,
        quantity = SINGLE_QUANTITY,
    )

    private companion object {
        const val USER_ID = "user-1"
        const val ORDER_NUMBER = "12345"
        const val OTHER_USER_ID = "user-2"
        const val OTHER_ORDER_NUMBER = "67890"

        const val MARGHERITA_ID = "margherita"
        const val MARGHERITA_NAME = "Margherita"
        const val MARGHERITA_PRICE = 9.99
        const val MARGHERITA_OLD_PRICE = 8.99
        const val PEPPERONI_PIZZA_ID = "pepperoni-pizza"
        const val PEPPERONI_NAME = "Pepperoni"
        const val HAWAIIAN_ID = "hawaiian"
        const val HAWAIIAN_NAME = "Hawaiian"
        const val COLA_ID = "cola"
        const val COLA_NAME = "Cola"
        const val COLA_PRICE = 2.49
        const val COLA_OLD_PRICE = 1.99
        const val COLA_ORDER_QUANTITY = 2
        const val CART_COLA_QUANTITY = 1
        const val SINGLE_QUANTITY = 1

        // A line of one, grown by one identical reordered item.
        const val MERGED_QUANTITY = 2

        const val EXTRA_CHEESE_ID = "extra-cheese"
        const val EXTRA_CHEESE_NAME = "Extra cheese"
        const val EXTRA_CHEESE_PRICE = 1.50
        const val EXTRA_CHEESE_OLD_PRICE = 1.00
        const val PEPPERONI_TOPPING_ID = "pepperoni"
        const val PEPPERONI_TOPPING_PRICE = 1.00
        const val PEPPERONI_QUANTITY = 2
        const val EXTRA_BACON_ID = "extra-bacon"
        const val EXTRA_BACON_NAME = "Extra bacon"

        const val FIRST_LINE_ID = "line-1"
        const val SECOND_LINE_ID = "line-2"
        const val COLA_LINE_ID = "line-cola"
        const val PEPPERONI_PIZZA_LINE_ID = "line-pepperoni"

        // The spec's totals: $9.99 + $1.50, and 3 x $2.49.
        const val MARGHERITA_WITH_EXTRA_CHEESE_TOTAL = 11.49
        const val THREE_COLAS_TOTAL = 7.47
        const val PRICE_TOLERANCE = 0.001

        val NOW: Instant = Instant.parse("2026-11-10T12:00:00Z")
    }
}
