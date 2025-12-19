package com.example.menu.detail

sealed interface PizzaDetailUiState {
    object Loading : PizzaDetailUiState
    object Error : PizzaDetailUiState
    data class Ready(
        val pizza: PizzaDetailDisplayModel,
        val toppings: List<ToppingDisplayModel>,
        val toppingQuantities: Map<String, Int>,
        val totalPriceFormatted: String,
        val quantity: Int,
    ) : PizzaDetailUiState
}
