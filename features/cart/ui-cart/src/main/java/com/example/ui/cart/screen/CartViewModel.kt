package com.example.ui.cart.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.AddCartItemUseCase
import com.example.domain.usecase.ObserveCartUseCase
import com.example.domain.usecase.ObserveRecommendedItemsUseCase
import com.example.domain.usecase.RemoveCartItemUseCase
import com.example.domain.usecase.UpdateCartItemQuantityUseCase
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
    private val addCartItemUseCase: AddCartItemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.onStart {
        observeCart()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = CartUiState.Loading,
    )

    private fun observeCart() {
        viewModelScope.launch {
            combine(
                observeCartUseCase(),
                observeRecommendedItemsUseCase(),
            ) { cart, recommendedItems ->
                if (cart.items.isEmpty()) {
                    CartUiState.Empty
                } else {
                    val cartDisplayModel = cart.toDisplayModel()
                    val recommendedItemsDisplayModel = recommendedItems.map { it.toRecommendedItemDisplayModel() }
                    CartUiState.Success(
                        cart = cartDisplayModel,
                        recommendedItems = recommendedItemsDisplayModel,
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onLineQuantityChange(
        item: CartLineDisplayModel,
        newQuantity: Int,
    ) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                removeCartItemUseCase(item.lineId)
            } else {
                updateCartItemQuantityUseCase(
                    lineId = item.lineId,
                    quantity = if (newQuantity > item.quantity) {
                        item.quantity + 1
                    } else {
                        (item.quantity - 1).coerceAtLeast(0)
                    },
                )
            }
        }
    }

    fun onRemoveLine(lineId: String) {
        viewModelScope.launch {
            removeCartItemUseCase(lineId)
        }
    }

    fun onAddToCart(item: RecommendedItemDisplayModel) {
        viewModelScope.launch {
            addCartItemUseCase(item.toCartItemDisplayModel())
        }
    }
}
