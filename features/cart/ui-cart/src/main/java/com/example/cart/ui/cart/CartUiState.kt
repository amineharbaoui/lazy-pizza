package com.example.cart.ui.cart

sealed interface CartUiState {
    object Loading : CartUiState
    object Empty : CartUiState
    data class Success(
        val cart: CartDisplayModel,
        val recommendedItems: List<RecommendedItemDisplayModel>,
    ) : CartUiState
    data class Error(val message: String) : CartUiState
}
