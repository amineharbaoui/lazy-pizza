package com.example.menu.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menu.domain.usecase.ObservePizzaDetailUseCase
import com.example.ui.utils.formatting.toFormattedCurrency
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PizzaDetailViewModel.Factory::class)
class PizzaDetailViewModel @AssistedInject constructor(
    @Assisted val productId: String,
    private val observePizzaDetailUseCase: ObservePizzaDetailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PizzaDetailUiState>(PizzaDetailUiState.Loading)
    val uiState: StateFlow<PizzaDetailUiState> = _uiState.asStateFlow()

    init {
        observePizza()
    }

    private fun observePizza() {
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

    @AssistedFactory
    interface Factory {
        fun create(productId: String): PizzaDetailViewModel
    }
}