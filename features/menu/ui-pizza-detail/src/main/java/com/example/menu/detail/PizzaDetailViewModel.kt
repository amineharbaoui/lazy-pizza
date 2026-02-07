package com.example.menu.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.usecase.AddCartItemUseCase
import com.example.cart.domain.usecase.ObserveCartItemUseCase
import com.example.menu.detail.mapper.PizzaDetailDomainToUiMapper
import com.example.menu.domain.usecase.ObservePizzaDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PizzaDetailViewModel @Inject constructor(
    private val observePizzaDetailUseCase: ObservePizzaDetailUseCase,
    private val observeCartItemUseCase: ObserveCartItemUseCase,
    private val addCartItemUseCase: AddCartItemUseCase,
    private val pizzaDetailMapper: PizzaDetailDomainToUiMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PizzaDetailUiState>(PizzaDetailUiState.Loading)
    val uiState: StateFlow<PizzaDetailUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var currentProductId: String? = null
    private var currentLineId: String? = null
    private var currentSessionId: String? = null

    fun initialize(
        args: PizzaDetailArgs,
        sessionId: String,
    ) {
        val productId = args.productId
        val lineId = (args as? PizzaDetailArgs.Edit)?.lineId

        if (currentProductId == productId && currentLineId == lineId && currentSessionId == sessionId) return
        currentProductId = productId
        currentLineId = lineId
        currentSessionId = sessionId

        observeJob?.cancel()
        _uiState.value = PizzaDetailUiState.Loading
        observeJob = viewModelScope.launch {
            val detailFlow = observePizzaDetailUseCase(id = productId)
            val cartItemFlow = if (lineId != null) {
                observeCartItemUseCase(lineId = lineId)
            } else {
                flowOf(null)
            }

            combine(detailFlow, cartItemFlow) { detail, cartItem ->
                detail to cartItem
            }.catch {
                _uiState.value = PizzaDetailUiState.Error
            }.collect { (detail, cartItem) ->
                val (pizzaDisplay, toppingsDisplay) = pizzaDetailMapper.mapToDisplayModels(detail)
                val previous = _uiState.value as? PizzaDetailUiState.Ready

                val isInitial = previous == null

                val quantity = if (isInitial && cartItem != null) {
                    cartItem.quantity
                } else {
                    previous?.quantity ?: 1
                }

                val allowedIds = toppingsDisplay.map { it.id }.toSet()
                val toppingQuantities = if (isInitial && cartItem is CartItem.Pizza) {
                    buildMap {
                        allowedIds.forEach { id ->
                            put(id, cartItem.toppings.find { it.id == id }?.quantity ?: 0)
                        }
                    }
                } else if (previous != null) {
                    buildMap {
                        allowedIds.forEach { id ->
                            put(id, previous.toppingQuantities[id] ?: 0)
                        }
                    }
                } else {
                    allowedIds.associateWith { 0 }
                }

                _uiState.value = PizzaDetailUiState.Ready(
                    lineId = lineId,
                    pizza = pizzaDisplay,
                    toppings = toppingsDisplay,
                    toppingQuantities = toppingQuantities,
                    quantity = quantity,
                    totalPriceFormatted = pizzaDetailMapper.formatTotalPrice(
                        unitPrice = pizzaDisplay.unitPrice,
                        toppings = toppingsDisplay,
                        toppingQuantities = toppingQuantities,
                        quantity = quantity,
                    ),
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

        val newMap = current.toppingQuantities.toMutableMap().apply { this[toppingId] = safeQty }

        _uiState.value = current.copy(
            toppingQuantities = newMap,
            totalPriceFormatted = pizzaDetailMapper.formatTotalPrice(
                unitPrice = current.pizza.unitPrice,
                toppings = current.toppings,
                toppingQuantities = newMap,
                quantity = current.quantity,
            ),
        )
    }

    fun updateQuantity(newQuantity: Int) {
        val current = _uiState.value as? PizzaDetailUiState.Ready ?: return
        val safeQty = newQuantity.coerceAtLeast(1)

        _uiState.value = current.copy(
            quantity = safeQty,
            totalPriceFormatted = pizzaDetailMapper.formatTotalPrice(
                unitPrice = current.pizza.unitPrice,
                toppings = current.toppings,
                toppingQuantities = current.toppingQuantities,
                quantity = safeQty,
            ),
        )
    }

    fun addItemToCart(pizzaDetails: PizzaDetailUiState.Ready) {
        viewModelScope.launch {
            addCartItemUseCase(pizzaDetailMapper.mapToPizzaCartItem(pizzaDetails))
        }
    }
}
