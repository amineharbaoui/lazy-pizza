package com.example.lazypizza.features.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.lazypizza.features.detail.domain.usecase.GetProductByIdUseCase
import com.example.lazypizza.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val productId = savedStateHandle.toRoute<Route.Detail>().productId

    val uiState: StateFlow<DetailUiState> = flow { emitAll(getProductByIdUseCase(productId)) }
        .map { product ->
            product?.let { DetailUiState.Success(it) } ?: DetailUiState.Error
        }
        .onStart { emit(DetailUiState.Loading) }
        .catch { emit(DetailUiState.Error) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            DetailUiState.Loading
        )
}