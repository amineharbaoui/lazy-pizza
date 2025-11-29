package com.example.ui.pizzadetail

sealed interface PizzaDetailUiState {
    object Loading : PizzaDetailUiState
    object Error : PizzaDetailUiState
    data class Success(
        val pizza: PizzaDetailDisplayModel,
        val toppings: List<ToppingDisplayModel>,
        val toppingQuantities: Map<String, Int>,
        val totalPriceFormatted: String,
        val quantity: Int,
    ) : PizzaDetailUiState
}
