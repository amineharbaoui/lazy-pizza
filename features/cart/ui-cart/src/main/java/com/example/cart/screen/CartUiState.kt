package com.example.cart.screen

sealed interface CartUiState {
    object Loading : CartUiState
    object Empty : CartUiState
    data class Success(
        val cart: CartDisplayModel,
        val recommendedItems: List<RecommendedItemDisplayModel>,
    ) : CartUiState
    data class Error(val message: String) : CartUiState
}
