package com.example.lazypizza.features.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lazypizza.features.detail.domain.usecase.GetProductByIdUseCase
import com.example.lazypizza.features.detail.domain.usecase.GetToppingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getProductByIdUseCase: GetProductByIdUseCase,
    getToppingsUseCase: GetToppingsUseCase,
) : ViewModel() {

    private val productId = "" //savedStateHandle.toRoute<Route.Detail>().productId

    val uiState: StateFlow<DetailUiState> = combine(
        flow = getProductByIdUseCase(productId),
        flow2 = getToppingsUseCase()
    ) { product, toppings ->
        if (product != null) {
            DetailUiState.Success(product, toppings)
        } else {
            DetailUiState.Error
        }
    }.onStart { emit(DetailUiState.Loading) }
        .catch { emit(DetailUiState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = DetailUiState.Loading
        )
}