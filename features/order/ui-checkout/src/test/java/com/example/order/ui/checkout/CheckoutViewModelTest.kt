package com.example.order.ui.checkout

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.example.auth.data.AuthTestData
import com.example.auth.domain.usecase.GetCurrentUserUidUseCase
import com.example.cart.data.CartTestData
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.model.CartTopping
import com.example.cart.domain.usecase.ClearCartUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.core.model.ProductCategory
import com.example.core.testing.MainDispatcherExtension
import com.example.core.ui.utils.formatting.DefaultCurrencyFormatter
import com.example.core.ui.utils.formatting.toPickupDisplayLabel
import com.example.order.data.OrderTestData
import com.example.order.domain.model.PickupType
import com.example.order.domain.usecase.CreateOrderUseCase
import com.example.order.domain.usecase.GetPickupScheduleUseCase
import com.example.order.domain.usecase.PlaceOrderUseCase
import com.example.order.ui.checkout.mapper.CartToOrderMapper
import com.example.order.ui.checkout.mapper.CartToOrderSummaryUiMapper
import com.example.order.ui.checkout.mapper.CheckoutUiStateFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private var fixedClock = Clock.fixed(NOW, ZoneOffset.UTC)
    private val clock = object : Clock() {
        override fun instant(): Instant = fixedClock.instant()

        override fun getZone(): ZoneId = fixedClock.zone

        override fun withZone(zone: ZoneId): Clock = fixedClock.withZone(zone)
    }
    private val authTestData = AuthTestData()
    private val cartTestData = CartTestData(clock)
    private val orderTestData = OrderTestData()
    private val currencyFormatter = DefaultCurrencyFormatter()

    private fun viewModel() = CheckoutViewModel(
        observeCartUseCase = ObserveCartUseCase(cartTestData.cartRepository, authTestData.sessionRepository),
        placeOrderUseCase = PlaceOrderUseCase(orderTestData.ordersRepository),
        createOrderUseCase = CreateOrderUseCase(clock),
        getPickupScheduleUseCase = GetPickupScheduleUseCase(clock),
        getCurrentUserUidUseCase = GetCurrentUserUidUseCase(authTestData.sessionRepository),
        checkoutUiStateFactory = CheckoutUiStateFactory(CartToOrderSummaryUiMapper(currencyFormatter)),
        cartToOrderMapper = CartToOrderMapper(),
        clearCartUseCase = ClearCartUseCase(cartTestData.cartRepository, authTestData.sessionRepository),
    )

    @Nested
    inner class UiState {

        @Test
        fun `uiState when the cart has items then shows their summary and total`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(CheckoutUiState.Loading)

                // When
                val state = awaitItem()

                // Then
                assertThat(state).isInstanceOf(CheckoutUiState.ReadyToOrder::class.java)
                val summary = (state as CheckoutUiState.ReadyToOrder).orderSummary
                assertThat(summary.lines).containsExactly(
                    OrderLineUi.PizzaProduct(
                        productId = MARGHERITA_ID,
                        title = MARGHERITA_TITLE,
                        unitPriceLabel = currencyFormatter.format(MARGHERITA_PRICE),
                        totalPriceLabel = currencyFormatter.format(MARGHERITA_WITH_EXTRA_CHEESE_TOTAL),
                        subtitleLines = listOf(extraCheeseSubtitle()),
                    ),
                    OrderLineUi.OtherProduct(
                        title = COLAS_TITLE,
                        unitPriceLabel = currencyFormatter.format(TWO_COLAS_TOTAL),
                        totalPriceLabel = null,
                        subtitleLines = emptyList(),
                    ),
                )
                assertThat(summary.totalLabel).isEqualTo(currencyFormatter.format(CART_TOTAL))
            }
        }

        @Test
        fun `uiState when the screen opens then shows the earliest available pickup selected`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(CheckoutUiState.Loading)

                // When
                val state = awaitItem() as CheckoutUiState.ReadyToOrder

                // Then
                assertThat(state.pickup.selectedOption).isEqualTo(PickupOption.ASAP)
                assertThat(state.pickup.asapCard.isSelected).isTrue()
                assertThat(state.pickup.asapCard.dateLabel).isEqualTo(TODAY_DATE_LABEL)
                assertThat(state.pickup.asapCard.timeLabel).isEqualTo(EARLIEST_TIME_LABEL)
                assertThat(state.pickup.scheduleCard.isSelected).isFalse()
                assertThat(state.pickup.scheduleCard.dateLabel).isEqualTo(CHOOSE_A_TIME_LABEL)
                assertThat(state.comment).isEmpty()
            }
        }

        @Test
        fun `uiState when the confirmed slot has passed on return then goes back to the earliest pickup`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()
            viewModel.uiState.test {
                awaitReady()
                viewModel.selectPickupOption(PickupOption.SCHEDULE)
                awaitItem()
                viewModel.selectScheduleTimeSlot(SOON_SLOT_ID)
                awaitItem()
                viewModel.confirmSchedule()
                awaitItem()
            }
            advanceTimeBy(BACKGROUND_TIME)
            fixedClock = Clock.fixed(AFTER_SOON_SLOT, ZoneOffset.UTC)

            // When
            viewModel.uiState.test {
                advanceUntilIdle()

                // Then
                val pickup = (expectMostRecentItem() as CheckoutUiState.ReadyToOrder).pickup
                assertThat(pickup.selectedOption).isEqualTo(PickupOption.ASAP)
                assertThat(pickup.asapCard.isSelected).isTrue()
                assertThat(pickup.scheduleCard.isSelected).isFalse()
                assertThat(pickup.scheduleCard.dateLabel).isEqualTo(CHOOSE_A_TIME_LABEL)
                assertThat(pickup.scheduleSheet.confirmation).isNull()
            }
        }

        @Test
        fun `uiState when the screen comes back after a failed order then shows the form again`() = runTest {
            // Given
            givenSignedInWithCart()
            orderTestData.givenFailure()
            val viewModel = viewModel()
            viewModel.uiState.test {
                awaitReady()
                viewModel.submitOrder()
                advanceUntilIdle()
                assertThat(expectMostRecentItem()).isInstanceOf(CheckoutUiState.Error::class.java)
            }
            advanceTimeBy(BACKGROUND_TIME)

            // When
            viewModel.uiState.test {
                advanceUntilIdle()

                // Then
                assertThat(expectMostRecentItem()).isInstanceOf(CheckoutUiState.ReadyToOrder::class.java)
            }
        }
    }

    @Nested
    inner class SelectPickupOption {

        @Test
        fun `selectPickupOption when schedule then selects the first available slot`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.selectPickupOption(PickupOption.SCHEDULE)

                // Then
                val pickup = (awaitItem() as CheckoutUiState.ReadyToOrder).pickup
                assertThat(pickup.selectedOption).isEqualTo(PickupOption.SCHEDULE)
                assertThat(pickup.asapCard.isSelected).isFalse()
                assertThat(pickup.scheduleCard.isSelected).isTrue()
                assertThat(pickup.scheduleSheet.selection)
                    .isEqualTo(PickupSelectionUiModel(selectedDayId = TODAY_ID, selectedTimeSlotId = EARLIEST_SLOT_ID))
            }
        }

        @Test
        fun `selectPickupOption when asap after a confirmed schedule then keeps the confirmed slot on the schedule card`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()
                selectTomorrowAtNoon(viewModel)
                viewModel.confirmSchedule()
                awaitItem()

                // When
                viewModel.selectPickupOption(PickupOption.ASAP)

                // Then
                val pickup = (awaitItem() as CheckoutUiState.ReadyToOrder).pickup
                assertThat(pickup.selectedOption).isEqualTo(PickupOption.ASAP)
                assertThat(pickup.asapCard.isSelected).isTrue()
                assertThat(pickup.scheduleCard.isSelected).isFalse()
                assertThat(pickup.scheduleCard.dateLabel).isEqualTo(TOMORROW_DATE_LABEL)
                assertThat(pickup.scheduleCard.timeLabel).isEqualTo(NOON_TIME_LABEL)
            }
        }
    }

    @Nested
    inner class SelectScheduleDay {

        @Test
        fun `selectScheduleDay when another day is selected then selects its first slot`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()
                viewModel.selectPickupOption(PickupOption.SCHEDULE)
                awaitItem()

                // When
                viewModel.selectScheduleDay(TOMORROW_ID)

                // Then
                val selection = (awaitItem() as CheckoutUiState.ReadyToOrder).pickup.scheduleSheet.selection
                assertThat(selection).isEqualTo(PickupSelectionUiModel(selectedDayId = TOMORROW_ID, selectedTimeSlotId = OPENING_SLOT_ID))
            }
        }
    }

    @Nested
    inner class ConfirmSchedule {

        @Test
        fun `confirmSchedule when a day and a slot are selected then shows them on the schedule card`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()
                selectTomorrowAtNoon(viewModel)

                // When
                viewModel.confirmSchedule()

                // Then
                val pickup = (awaitItem() as CheckoutUiState.ReadyToOrder).pickup
                assertThat(pickup.selectedOption).isEqualTo(PickupOption.SCHEDULE)
                assertThat(pickup.scheduleCard.isSelected).isTrue()
                assertThat(pickup.scheduleCard.dateLabel).isEqualTo(TOMORROW_DATE_LABEL)
                assertThat(pickup.scheduleCard.timeLabel).isEqualTo(NOON_TIME_LABEL)
                assertThat(pickup.scheduleSheet.confirmation)
                    .isEqualTo(PickupConfirmationUiModel(dayId = TOMORROW_ID, timeSlotId = NOON_SLOT_ID))
            }
        }
    }

    @Nested
    inner class UpdateComment {

        @Test
        fun `updateComment when the user types then shows the comment`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.updateComment(COMMENT)

                // Then
                assertThat((awaitItem() as CheckoutUiState.ReadyToOrder).comment).isEqualTo(COMMENT)
            }
        }
    }

    @Nested
    inner class SubmitOrder {

        @Test
        fun `submitOrder when asap then stores the order of the cart`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()
                viewModel.updateComment(COMMENT)
                awaitItem()

                // When
                viewModel.submitOrder()
                advanceUntilIdle()

                // Then
                assertThat(storedOrders()).singleElement().satisfies({ order ->
                    assertThat(order.orderNumber).isEqualTo(ORDER_NUMBER)
                    assertThat(order.userId).isEqualTo(USER_ID)
                    assertThat(order.pickupType).isEqualTo(PickupType.ASAP)
                    assertThat(order.pickupAt).isEqualTo(ASAP_PICKUP_AT)
                    assertThat(order.comment).isEqualTo(COMMENT)
                    assertThat(order.total).isCloseTo(CART_TOTAL, within(PRICE_TOLERANCE))
                    assertThat(order.items.map { it.productId to it.quantity })
                        .containsExactly(MARGHERITA_ID to SINGLE_QUANTITY, COLA_ID to COLA_QUANTITY)
                    assertThat(order.items.first().toppings.map { it.id }).containsExactly(EXTRA_CHEESE_ID)
                })
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `submitOrder when a scheduled pickup is confirmed then stores the order for that time`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()
                selectTomorrowAtNoon(viewModel)
                viewModel.confirmSchedule()
                awaitItem()

                // When
                viewModel.submitOrder()
                advanceUntilIdle()

                // Then
                assertThat(storedOrders()).singleElement().satisfies({ order ->
                    assertThat(order.pickupType).isEqualTo(PickupType.SCHEDULED)
                    assertThat(order.pickupAt).isEqualTo(TOMORROW_AT_NOON)
                })
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `submitOrder when the order is placed then empties the cart`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.submitOrder()
                advanceUntilIdle()

                // Then
                assertThat(cartTestData.cartLines(CartOwner.User(USER_ID))).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `submitOrder when the order is placed then shows its number and pickup time`() = runTest {
            // Given
            givenSignedInWithCart()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.submitOrder()
                advanceUntilIdle()

                // Then
                assertThat(expectMostRecentItem()).isEqualTo(
                    CheckoutUiState.OrderPlaced(
                        orderNumber = FORMATTED_ORDER_NUMBER,
                        pickupTime = ASAP_PICKUP_AT.toPickupDisplayLabel().uppercase(),
                    ),
                )
            }
        }

        @Test
        fun `submitOrder when placing fails then shows an error`() = runTest {
            // Given
            givenSignedInWithCart()
            orderTestData.givenFailure()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.submitOrder()
                advanceUntilIdle()

                // Then
                assertThat(expectMostRecentItem()).isInstanceOf(CheckoutUiState.Error::class.java)
            }
        }

        @Test
        fun `submitOrder when placing fails then keeps the cart`() = runTest {
            // Given
            givenSignedInWithCart()
            orderTestData.givenFailure()
            val viewModel = viewModel()

            viewModel.uiState.test {
                awaitReady()

                // When
                viewModel.submitOrder()
                advanceUntilIdle()

                // Then
                assertThat(cartTestData.cartLines(CartOwner.User(USER_ID)).map { it.productId })
                    .containsExactly(MARGHERITA_ID, COLA_ID)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    private fun givenSignedInWithCart() {
        authTestData.givenSignedIn(USER_ID)
        cartTestData.givenCart(CartOwner.User(USER_ID), margheritaLine(), colaLine())
    }

    private suspend fun ReceiveTurbine<CheckoutUiState>.awaitReady() {
        assertThat(awaitItem()).isEqualTo(CheckoutUiState.Loading)
        assertThat(awaitItem()).isInstanceOf(CheckoutUiState.ReadyToOrder::class.java)
    }

    private suspend fun ReceiveTurbine<CheckoutUiState>.selectTomorrowAtNoon(viewModel: CheckoutViewModel) {
        viewModel.selectPickupOption(PickupOption.SCHEDULE)
        awaitItem()
        viewModel.selectScheduleDay(TOMORROW_ID)
        awaitItem()
        viewModel.selectScheduleTimeSlot(NOON_SLOT_ID)
        awaitItem()
    }

    private fun extraCheeseSubtitle() = "${SINGLE_QUANTITY}x $EXTRA_CHEESE_NAME (${currencyFormatter.format(EXTRA_CHEESE_PRICE)})"

    private suspend fun storedOrders() = orderTestData.ordersRepository.observeMyOrders(USER_ID).first()

    private fun margheritaLine() = CartItem.Pizza(
        lineId = MARGHERITA_LINE_ID,
        productId = MARGHERITA_ID,
        name = MARGHERITA_NAME,
        imageUrl = "",
        quantity = SINGLE_QUANTITY,
        category = ProductCategory.PIZZA,
        unitPrice = MARGHERITA_PRICE,
        toppings = listOf(
            CartTopping(
                id = EXTRA_CHEESE_ID,
                name = EXTRA_CHEESE_NAME,
                unitPrice = EXTRA_CHEESE_PRICE,
                quantity = SINGLE_QUANTITY,
            ),
        ),
    )

    private fun colaLine() = CartItem.Other(
        lineId = COLA_LINE_ID,
        productId = COLA_ID,
        name = COLA_NAME,
        imageUrl = "",
        quantity = COLA_QUANTITY,
        category = ProductCategory.DRINK,
        unitPrice = COLA_PRICE,
    )

    private companion object {
        val NOW: Instant = Instant.parse("2026-11-13T10:00:12.345Z")
        val ASAP_PICKUP_AT: Instant = Instant.parse("2026-11-13T10:30:00Z")
        val TOMORROW_AT_NOON: Instant = Instant.parse("2026-11-14T12:00:00Z")
        val AFTER_SOON_SLOT: Instant = Instant.parse("2026-11-13T10:40:00Z")
        val BACKGROUND_TIME = 6.seconds
        const val ORDER_NUMBER = "012345"
        const val FORMATTED_ORDER_NUMBER = "#012-345"

        const val USER_ID = "user-1"
        const val COMMENT = "Ring twice"

        const val TODAY_ID = "2026-11-13"
        const val TOMORROW_ID = "2026-11-14"
        const val EARLIEST_SLOT_ID = "10:30"
        const val SOON_SLOT_ID = "10:45"
        const val OPENING_SLOT_ID = "09:00"
        const val NOON_SLOT_ID = "12:00"
        const val TODAY_DATE_LABEL = "Today - Nov 13"
        const val TOMORROW_DATE_LABEL = "Tomorrow - Nov 14"
        const val EARLIEST_TIME_LABEL = "10:30 AM"
        const val NOON_TIME_LABEL = "12:00 PM"
        const val CHOOSE_A_TIME_LABEL = "Choose a time"

        const val MARGHERITA_LINE_ID = "line-margherita"
        const val MARGHERITA_ID = "margherita"
        const val MARGHERITA_NAME = "Margherita"
        const val MARGHERITA_TITLE = "1x Margherita"
        const val MARGHERITA_PRICE = 9.99
        const val EXTRA_CHEESE_ID = "extra-cheese"
        const val EXTRA_CHEESE_NAME = "Extra cheese"
        const val EXTRA_CHEESE_PRICE = 1.50
        const val MARGHERITA_WITH_EXTRA_CHEESE_TOTAL = 11.49
        const val COLA_LINE_ID = "line-cola"
        const val COLA_ID = "cola"
        const val COLA_NAME = "Cola"
        const val COLAS_TITLE = "2x Cola"
        const val COLA_PRICE = 2.49
        const val COLA_QUANTITY = 2
        const val TWO_COLAS_TOTAL = 4.98
        const val CART_TOTAL = 16.47
        const val SINGLE_QUANTITY = 1
        const val PRICE_TOLERANCE = 0.001
    }
}
