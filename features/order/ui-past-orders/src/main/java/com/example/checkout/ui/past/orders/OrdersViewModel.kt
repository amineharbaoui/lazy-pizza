package com.example.checkout.ui.past.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.GetCurrentUserUidUseCase
import com.example.order.domain.usecase.ObserveOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val observeOrdersUseCase: ObserveOrdersUseCase,
    private val orderToUiMapper: OrderToUiMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState.onStart {
        observeOrders()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = OrderUiState.Loading,
    )

    private fun observeOrders() {
        viewModelScope.launch {
            val userId = getCurrentUserUidUseCase()
            if (userId == null) {
                _uiState.value = OrderUiState.NotLoggedIn
                return@launch
            }

            observeOrdersUseCase(userId)
                .map { list ->
                    Log.d(javaClass.name, "observeOrders: $list")
                    list
                        .sortedByDescending { it.createdAt }
                        .map(orderToUiMapper::map)
                }
                .catch { e ->
                    _uiState.value = OrderUiState.Error("Failed to load orders.")
                }
                .collect { uiOrders ->
                    _uiState.value = OrderUiState.Ready(uiOrders)
                }
        }
    }
}
