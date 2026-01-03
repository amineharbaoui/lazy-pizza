package com.example.cart.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cart.screen.mapper.CartDomainToUiMapper
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
    private val cartMapper: CartDomainToUiMapper,
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
                cartMapper.mapToCartUiState(cart = cart, recommendedItems = recommendedItems)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateLineQuantity(
        item: CartLineDisplayModel,
        newQuantity: Int,
    ) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                removeCartItemUseCase(item.lineId)
            } else {
                updateCartItemQuantityUseCase(
                    lineId = item.lineId,
                    quantity = newQuantity,
                )
            }
        }
    }

    fun removeLine(lineId: String) {
        viewModelScope.launch {
            removeCartItemUseCase(lineId)
        }
    }

    fun addItemToCart(item: RecommendedItemDisplayModel) {
        viewModelScope.launch {
            addCartItemUseCase(cartMapper.mapToCartItem(item))
        }
    }
}
