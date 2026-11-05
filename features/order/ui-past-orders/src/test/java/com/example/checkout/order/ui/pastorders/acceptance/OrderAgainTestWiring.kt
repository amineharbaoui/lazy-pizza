package com.example.checkout.order.ui.pastorders.acceptance

import com.example.auth.data.datasource.FakePhoneAuthDataSource
import com.example.auth.data.repository.SessionRepositoryImpl
import com.example.auth.domain.usecase.GetCurrentUserUidUseCase
import com.example.cart.data.datasource.FakeCartLocalDataSource
import com.example.cart.data.datasource.FakeCartTransferLocalDataSource
import com.example.cart.data.mapper.CartOwnerToKeyMapper
import com.example.cart.data.repository.CartRepositoryImpl
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.usecase.AddCartItemsUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.checkout.order.ui.pastorders.OrderAgainUiState
import com.example.checkout.order.ui.pastorders.OrderToUiMapper
import com.example.checkout.order.ui.pastorders.OrderUiState
import com.example.checkout.order.ui.pastorders.OrdersViewModel
import com.example.core.testing.MainDispatcherExtension
import com.example.core.ui.utils.formatting.DefaultCurrencyFormatter
import com.example.menu.data.datasource.FakeProductRemoteDataSource
import com.example.menu.data.mapper.ProductDtoToDomainMapper
import com.example.menu.data.repository.MenuRepositoryImpl
import com.example.menu.domain.usecase.GetMenuSnapshotUseCase
import com.example.order.data.datasource.FakeOrdersRemoteDataSource
import com.example.order.data.mapper.OrderDomainToDtoMapper
import com.example.order.data.mapper.OrderDtoToDomainMapper
import com.example.order.data.model.OrderDto
import com.example.order.data.model.OrderItemDto
import com.example.order.data.repository.OrdersRepositoryImpl
import com.example.order.domain.usecase.ObserveOrdersUseCase
import com.example.order.domain.usecase.ReorderUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat

/**
 * The real Orders graph wired by hand: real ViewModel, use cases and repositories, every data
 * source faked. Script the fakes, then call [openOrdersScreen].
 */
class OrderAgainTestWiring(private val mainDispatcher: MainDispatcherExtension) {

    val productRemoteDataSource = FakeProductRemoteDataSource()
    val cartLocalDataSource = FakeCartLocalDataSource()
    val ordersRemoteDataSource = FakeOrdersRemoteDataSource()
    val phoneAuthDataSource = FakePhoneAuthDataSource()

    private val cartOwnerToKeyMapper = CartOwnerToKeyMapper()

    private val sessionRepository = SessionRepositoryImpl(phoneAuthDataSource)
    private val menuRepository = MenuRepositoryImpl(productRemoteDataSource, ProductDtoToDomainMapper())
    private val cartRepository = CartRepositoryImpl(
        localDataSource = cartLocalDataSource,
        transferLocalDataSource = FakeCartTransferLocalDataSource(),
        ownerToKeyMapper = cartOwnerToKeyMapper,
    )
    private val ordersRepository = OrdersRepositoryImpl(
        ordersRemoteDataSource = ordersRemoteDataSource,
        toDto = OrderDomainToDtoMapper(),
        toDomain = OrderDtoToDomainMapper(),
    )

    val observeCartUseCase = ObserveCartUseCase(cartRepository, sessionRepository)

    val viewModel = OrdersViewModel(
        getCurrentUserUidUseCase = GetCurrentUserUidUseCase(sessionRepository),
        observeOrdersUseCase = ObserveOrdersUseCase(ordersRepository),
        orderToUiMapper = OrderToUiMapper(DefaultCurrencyFormatter()),
        reorderUseCase = ReorderUseCase(
            getMenuSnapshotUseCase = GetMenuSnapshotUseCase(menuRepository),
            addCartItemsUseCase = AddCartItemsUseCase(cartRepository, sessionRepository),
        ),
    )

    private val screenScope = CoroutineScope(mainDispatcher.dispatcher)

    /** Signed-in [USER_ID] with today's menu, [order] in history and [cart] already in the cart. */
    fun givenSignedInUser(
        order: OrderDto,
        cart: List<CartItem> = emptyList(),
    ) {
        phoneAuthDataSource.signedInUid = USER_ID
        productRemoteDataSource.setProducts(*todaysMenu)
        ordersRemoteDataSource.setOrders(USER_ID, listOf(order))
        cartLocalDataSource.seed(cartOwnerToKeyMapper.map(CartOwner.User(USER_ID)), cart)
    }

    fun openOrdersScreenWith(
        vararg orderItems: OrderItemDto,
        cart: List<CartItem> = emptyList(),
    ) {
        givenSignedInUser(order = pastOrder(*orderItems), cart = cart)
        openOrdersScreen()
    }

    fun orderAgainAndSettle() {
        viewModel.orderAgain(ORDER_NUMBER)
        settle()
    }

    /** Subscribes to `uiState` like the screen does and lets the orders load. */
    fun openOrdersScreen() {
        screenScope.launch { viewModel.uiState.collect {} }
        settle()
    }

    fun closeOrdersScreen() {
        screenScope.cancel()
    }

    fun settle() {
        mainDispatcher.scheduler.advanceUntilIdle()
    }

    fun orderAgainState(): OrderAgainUiState {
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(OrderUiState.Ready::class.java)
        return (state as OrderUiState.Ready).orderAgain
    }

    suspend fun cartLines(): List<CartLine> = observeCartUseCase().first().items.map { it.toCartLine() }

    suspend fun cartItems(): List<CartItem> = observeCartUseCase().first().items
}
