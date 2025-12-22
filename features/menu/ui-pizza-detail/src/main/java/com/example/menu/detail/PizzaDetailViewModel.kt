package com.example.menu.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.AddCartItemUseCase
import com.example.domain.usecase.ObservePizzaDetailUseCase
import com.example.menu.detail.mapper.PizzaDetailDomainToUiMapper
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
    private val pizzaDetailMapper: PizzaDetailDomainToUiMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PizzaDetailUiState>(PizzaDetailUiState.Loading)
    val uiState: StateFlow<PizzaDetailUiState> = _uiState.asStateFlow()

    fun observePizza(productId: String) {
        viewModelScope.launch {
            observePizzaDetailUseCase(id = productId)
                .catch { _uiState.value = PizzaDetailUiState.Error }
                .collect { detail ->
                    val (pizzaDisplay, toppingsDisplay) = pizzaDetailMapper.mapToDisplayModels(detail)
                    val toppingQuantities = toppingsDisplay.associate { it.id to 0 }
                    val quantity = 1

                    _uiState.value = PizzaDetailUiState.Ready(
                        pizza = pizzaDisplay,
                        toppings = toppingsDisplay,
                        toppingQuantities = toppingQuantities,
                        totalPriceFormatted = pizzaDetailMapper.formatTotalPrice(
                            unitPrice = pizzaDisplay.unitPrice,
                            toppings = toppingsDisplay,
                            toppingQuantities = toppingQuantities,
                            quantity = quantity,
                        ),
                        quantity = quantity,
                    )
                }
        }
    }

    fun updateToppingQuantity(
        toppingId: String,
        newQuantity: Int,
    ) {
        val current = _uiState.value as? PizzaDetailUiState.Ready ?: return
        val safeQty = newQuantity.coerceAtLeast(0)

        val newMap = current.toppingQuantities.toMutableMap().apply {
            this[toppingId] = safeQty
        }

        _uiState.update {
            current.copy(
                toppingQuantities = newMap,
                totalPriceFormatted = pizzaDetailMapper.formatTotalPrice(
                    unitPrice = current.pizza.unitPrice,
                    toppings = current.toppings,
                    toppingQuantities = newMap,
                    quantity = current.quantity,
                ),
            )
        }
    }

    fun updateQuantity(newQuantity: Int) {
        val current = _uiState.value as? PizzaDetailUiState.Ready ?: return
        val safeQuantity = newQuantity.coerceAtLeast(1)

        _uiState.update {
            current.copy(
                quantity = safeQuantity,
                totalPriceFormatted = pizzaDetailMapper.formatTotalPrice(
                    unitPrice = current.pizza.unitPrice,
                    toppings = current.toppings,
                    toppingQuantities = current.toppingQuantities,
                    quantity = safeQuantity,
                ),
            )
        }
    }

    fun addItemToCart(pizzaDetails: PizzaDetailUiState.Ready) {
        viewModelScope.launch {
            addCartItemUseCase(pizzaDetailMapper.mapToPizzaCartItem(pizzaDetails))
        }
    }
}
