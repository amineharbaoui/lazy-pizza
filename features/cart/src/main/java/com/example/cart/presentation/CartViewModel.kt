package com.example.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cart.domain.usecase.ClearCartUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.cart.domain.usecase.ObserveRecommendedItemsUseCase
import com.example.cart.domain.usecase.RemoveCartItemUseCase
import com.example.cart.domain.usecase.UpdateCartItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val observeRecommendedItemsUseCase: ObserveRecommendedItemsUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.onStart {
        observeCart()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = CartUiState.Loading
    )

    private fun observeCart() {
        viewModelScope.launch {
            combine(
                observeCartUseCase(),
                observeRecommendedItemsUseCase()
            ) { cart, recommendedItems ->
                if (cart.items.isEmpty()) {
                    CartUiState.Empty
                } else {
                    val cartDisplayModel = cart.toDisplayModel()
                    val recommendedItemsDisplayModel = recommendedItems.map { it.toRecommendedItemDisplayModel() }
                    CartUiState.Success(
                        cart = cartDisplayModel,
                        recommendedItems = recommendedItemsDisplayModel
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onLineQuantityChange(
        lineId: String,
        newQuantity: Int,
    ) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                removeCartItemUseCase(lineId)
            } else {
                updateCartItemQuantityUseCase(lineId, newQuantity)
            }
        }
    }

    fun onRemoveLine(lineId: String) {
        viewModelScope.launch {
            removeCartItemUseCase(lineId)
        }
    }

    fun onClearCart() {
        viewModelScope.launch {
            clearCartUseCase()
        }
    }
}