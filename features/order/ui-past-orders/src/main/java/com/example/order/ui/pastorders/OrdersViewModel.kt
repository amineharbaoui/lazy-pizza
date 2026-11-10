package com.example.order.ui.pastorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.ObserveCurrentUserUidUseCase
import com.example.order.domain.model.Order
import com.example.order.domain.model.ReorderResult
import com.example.order.domain.usecase.ObserveOrdersUseCase
import com.example.order.domain.usecase.ReorderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OrdersViewModel @Inject constructor(
    observeCurrentUserUidUseCase: ObserveCurrentUserUidUseCase,
    private val observeOrdersUseCase: ObserveOrdersUseCase,
    private val orderToUiMapper: OrderToUiMapper,
    private val reorderUseCase: ReorderUseCase,
) : ViewModel() {

    private val orders: StateFlow<Orders> = observeCurrentUserUidUseCase()
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(Orders.NotLoggedIn)
            } else {
                observeOrdersUseCase(userId)
                    .map<_, Orders> { orders -> Orders.Loaded(orders.sortedByDescending { it.createdAt }) }
                    .catch { emit(Orders.Failed) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = Orders.Loading,
        )

    private val reorder = MutableStateFlow(ReorderProgress())

    val uiState: StateFlow<OrderUiState> = combine(orders, reorder) { orders, reorder ->
        when (orders) {
            Orders.Loading -> OrderUiState.Loading
            Orders.NotLoggedIn -> OrderUiState.NotLoggedIn
            is Orders.Loaded -> OrderUiState.Ready(
                orders = orders.orders.map(orderToUiMapper::map),
                isReordering = reorder.isRunning,
                dialog = reorder.dialog,
                navigation = reorder.navigation,
            )
            Orders.Failed -> OrderUiState.Error
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = OrderUiState.Loading,
    )

    fun reorder(orderNumber: String) {
        // One reorder at a time: a second tap while one runs does nothing.
        if (reorder.value.isRunning) return
        val order = (orders.value as? Orders.Loaded)?.orders?.firstOrNull { it.orderNumber == orderNumber } ?: return

        reorder.update { it.copy(isRunning = true) }
        viewModelScope.launch {
            val progress = when (reorderUseCase(order)) {
                ReorderResult.Added -> ReorderProgress(navigation = OrdersNavigation.CART)
                ReorderResult.NothingAvailable -> ReorderProgress(dialog = ReorderDialog.NOTHING_AVAILABLE)
                ReorderResult.MenuUnavailable -> ReorderProgress(dialog = ReorderDialog.FAILED)
            }
            reorder.value = progress
        }
    }

    fun dismissDialog() {
        reorder.update { it.copy(dialog = null) }
    }

    fun navigationHandled() {
        reorder.update { it.copy(navigation = null) }
    }

    private sealed interface Orders {
        data object Loading : Orders
        data object NotLoggedIn : Orders
        data class Loaded(val orders: List<Order>) : Orders
        data object Failed : Orders
    }

    private data class ReorderProgress(
        val isRunning: Boolean = false,
        val dialog: ReorderDialog? = null,
        val navigation: OrdersNavigation? = null,
    )
}
