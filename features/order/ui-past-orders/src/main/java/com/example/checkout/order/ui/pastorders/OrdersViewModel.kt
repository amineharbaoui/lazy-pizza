package com.example.checkout.order.ui.pastorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.GetCurrentUserUidUseCase
import com.example.order.domain.model.Order
import com.example.order.domain.model.ReorderError
import com.example.order.domain.model.ReorderResult
import com.example.order.domain.model.SkippedReorderItem
import com.example.order.domain.usecase.ObserveOrdersUseCase
import com.example.order.domain.usecase.ReorderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val observeOrdersUseCase: ObserveOrdersUseCase,
    private val orderToUiMapper: OrderToUiMapper,
    private val reorderUseCase: ReorderUseCase,
) : ViewModel() {

    private val _events = MutableSharedFlow<OrdersUiEvent>()
    val events: SharedFlow<OrdersUiEvent> = _events

    private var ordersByNumber: Map<String, Order> = emptyMap()

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState.onStart {
        observeOrders()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = OrderUiState.Loading,
    )

    fun orderAgain(orderNumber: String) {
        val state = _uiState.value
        // Ignores taps while a reorder runs or its dialog is open.
        if (state !is OrderUiState.Ready || state.orderAgain != OrderAgainUiState.Idle) return
        val order = ordersByNumber[orderNumber] ?: return
        updateOrderAgain(OrderAgainUiState.InProgress)

        viewModelScope.launch {
            when (val result = reorderUseCase(order)) {
                is ReorderResult.Added -> if (result.skipped.isEmpty()) {
                    updateOrderAgain(OrderAgainUiState.Idle)
                    _events.emit(OrdersUiEvent.NavigateToCart)
                } else {
                    updateOrderAgain(OrderAgainUiState.SomeSkipped(result.skipped.map(::toSkippedItemUi)))
                }

                is ReorderResult.NothingAvailable -> {
                    updateOrderAgain(OrderAgainUiState.NothingAvailable(result.skipped.map(::toSkippedItemUi)))
                }

                is ReorderResult.Failed -> {
                    updateOrderAgain(OrderAgainUiState.Idle)
                    _events.emit(OrdersUiEvent.ShowOrderAgainError(result.error.toErrorUi()))
                }
            }
        }
    }

    /** Closes the skipped-items dialog and opens the cart. */
    fun goToCartFromSkippedItems() {
        updateOrderAgain(OrderAgainUiState.Idle)
        viewModelScope.launch { _events.emit(OrdersUiEvent.NavigateToCart) }
    }

    /** "Close" or back on the order-again dialog: stays on Orders. */
    fun dismissOrderAgainDialog() {
        updateOrderAgain(OrderAgainUiState.Idle)
    }

    private fun updateOrderAgain(orderAgain: OrderAgainUiState) {
        _uiState.update { state -> if (state is OrderUiState.Ready) state.copy(orderAgain = orderAgain) else state }
    }

    private fun toSkippedItemUi(item: SkippedReorderItem): SkippedItemUi = when (item) {
        is SkippedReorderItem.Product -> SkippedItemUi.Product(item.name)
        is SkippedReorderItem.Topping -> SkippedItemUi.Topping(item.toppingName, item.pizzaName)
    }

    private fun ReorderError.toErrorUi(): OrderAgainErrorUi = when (this) {
        ReorderError.MenuUnavailable -> OrderAgainErrorUi.MENU_UNAVAILABLE
        ReorderError.CartUpdateFailed -> OrderAgainErrorUi.CART_UPDATE_FAILED
    }

    private fun observeOrders() {
        viewModelScope.launch {
            val userId = getCurrentUserUidUseCase()
            if (userId == null) {
                _uiState.value = OrderUiState.NotLoggedIn
                return@launch
            }

            observeOrdersUseCase(userId)
                .map { list ->
                    ordersByNumber = list.associateBy { it.orderNumber }
                    list
                        .sortedByDescending { it.createdAt }
                        .map(orderToUiMapper::map)
                }
                .catch { e ->
                    _uiState.value = OrderUiState.Error("Failed to load orders.")
                }
                .collect { uiOrders ->
                    _uiState.update { state ->
                        if (state is OrderUiState.Ready) state.copy(orders = uiOrders) else OrderUiState.Ready(uiOrders)
                    }
                }
        }
    }
}
