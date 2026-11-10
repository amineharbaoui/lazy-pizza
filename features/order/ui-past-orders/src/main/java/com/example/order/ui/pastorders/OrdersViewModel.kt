package com.example.order.ui.pastorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.ObserveCurrentUserUidUseCase
import com.example.order.domain.usecase.ObserveOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OrdersViewModel @Inject constructor(
    observeCurrentUserUidUseCase: ObserveCurrentUserUidUseCase,
    private val observeOrdersUseCase: ObserveOrdersUseCase,
    private val orderToUiMapper: OrderToUiMapper,
) : ViewModel() {

    val uiState: StateFlow<OrderUiState> = observeCurrentUserUidUseCase()
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(OrderUiState.NotLoggedIn)
            } else {
                observeOrdersUseCase(userId)
                    .map<_, OrderUiState> { orders ->
                        OrderUiState.Ready(
                            orders
                                .sortedByDescending { it.createdAt }
                                .map(orderToUiMapper::map),
                        )
                    }
                    .catch { emit(OrderUiState.Error) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = OrderUiState.Loading,
        )
}
