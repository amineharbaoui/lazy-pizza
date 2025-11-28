package com.example.cart.presentation

sealed interface CartUiState {
    object Loading : CartUiState
    object Empty : CartUiState
    data class Success(
        val cart: CartDisplayModel,
        val recommendedItems: List<RecommendedItemDisplayModel>,
    ) : CartUiState
}