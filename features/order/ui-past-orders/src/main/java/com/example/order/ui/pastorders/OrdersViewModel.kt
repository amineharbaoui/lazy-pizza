package com.example.order.ui.pastorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.GetCurrentUserUidUseCase
import com.example.order.domain.usecase.ObserveOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val observeOrdersUseCase: ObserveOrdersUseCase,
    private val orderToUiMapper: OrderToUiMapper,
) : ViewModel() {

    val uiState: StateFlow<OrderUiState> = flow {
        val userId = getCurrentUserUidUseCase()
        if (userId == null) {
            emit(OrderUiState.NotLoggedIn)
            return@flow
        }
        emitAll(
            observeOrdersUseCase(userId)
                .map<_, OrderUiState> { orders ->
                    OrderUiState.Ready(
                        orders
                            .sortedByDescending { it.createdAt }
                            .map(orderToUiMapper::map),
                    )
                }
                .catch { emit(OrderUiState.Error) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = OrderUiState.Loading,
    )
}
