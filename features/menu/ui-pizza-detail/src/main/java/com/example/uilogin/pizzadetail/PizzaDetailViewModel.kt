package com.example.uilogin.pizzadetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.AddCartItemUseCase
import com.example.domain.usecase.ObservePizzaDetailUseCase
import com.example.uilogin.utils.formatting.toFormattedCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PizzaDetailViewModel @Inject constructor(
    private val observePizzaDetailUseCase: ObservePizzaDetailUseCase,
    private val addCartItemUseCase: AddCartItemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PizzaDetailUiState>(PizzaDetailUiState.Loading)
    val uiState: StateFlow<PizzaDetailUiState> = _uiState.asStateFlow()

    fun observePizza(productId: String) {
        viewModelScope.launch {
            observePizzaDetailUseCase(id = productId)
                .catch { _uiState.value = PizzaDetailUiState.Error }
                .collect { detail ->
                    val (pizzaDisplay, toppingsDisplay) = detail.toDisplayModels()
                    val toppingQuantities = toppingsDisplay.associate { it.id to 0 }
                    val quantity = 1

                    _uiState.value = PizzaDetailUiState.Success(
                        pizza = pizzaDisplay,
                        toppings = toppingsDisplay,
                        toppingQuantities = toppingQuantities,
                        totalPriceFormatted = computeTotalPrice(
                            basePrice = pizzaDisplay.price,
                            toppings = toppingsDisplay,
                            toppingQuantities = toppingQuantities,
                            quantity = quantity,
                        ),
                        quantity = quantity,
                    )
                }
        }
    }

    fun onToppingQuantityChange(
        toppingId: String,
        newQuantity: Int,
    ) {
        val current = _uiState.value as? PizzaDetailUiState.Success ?: return
        val safeQty = newQuantity.coerceAtLeast(0)

        val newMap = current.toppingQuantities.toMutableMap().apply {
            this[toppingId] = safeQty
        }

        _uiState.update {
            current.copy(
                toppingQuantities = newMap,
                totalPriceFormatted = computeTotalPrice(
                    basePrice = current.pizza.price,
                    toppings = current.toppings,
                    toppingQuantities = newMap,
                    quantity = current.quantity,
                ),
            )
        }
    }

    fun onQuantityChange(newQuantity: Int) {
        val current = _uiState.value as? PizzaDetailUiState.Success ?: return
        val safeQuantity = newQuantity.coerceAtLeast(1)

        _uiState.update {
            current.copy(
                quantity = safeQuantity,
                totalPriceFormatted = computeTotalPrice(
                    basePrice = current.pizza.price,
                    toppings = current.toppings,
                    toppingQuantities = current.toppingQuantities,
                    quantity = safeQuantity,
                ),
            )
        }
    }

    fun onAddToCartClick(pizzaDetails: PizzaDetailUiState.Success) {
        viewModelScope.launch {
            addCartItemUseCase(pizzaDetails.toPizzaCartItem())
        }
    }

    private fun computeTotalPrice(
        basePrice: Double,
        toppings: List<ToppingDisplayModel>,
        toppingQuantities: Map<String, Int>,
        quantity: Int,
    ): String {
        val toppingsTotalForOnePizza = toppings.sumOf { topping ->
            val toppingQty = toppingQuantities[topping.id] ?: 0
            topping.price * toppingQty
        }

        val total = (basePrice + toppingsTotalForOnePizza) * quantity
        return total.toFormattedCurrency()
    }
}
