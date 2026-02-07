package com.example.menu.ui.pizzadetail.mapper

import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartTopping
import com.example.menu.domain.model.PizzaDetail
import com.example.menu.domain.model.Topping
import com.example.menu.ui.pizzadetail.PizzaDetailDisplayModel
import com.example.menu.ui.pizzadetail.PizzaDetailUiState
import com.example.menu.ui.pizzadetail.ToppingDisplayModel
import com.example.menu.utils.formatting.CurrencyFormatter
import com.example.model.ProductCategory
import java.util.UUID
import javax.inject.Inject

class PizzaDetailDomainToUiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
) {

    fun mapToDisplayModels(detail: PizzaDetail): Pair<PizzaDetailDisplayModel, List<ToppingDisplayModel>> {
        val pizzaDisplay = PizzaDetailDisplayModel(
            id = detail.pizza.id,
            name = detail.pizza.name,
            description = detail.pizza.description,
            imageUrl = detail.pizza.imageUrl,
            unitPrice = detail.pizza.unitPrice,
            unitPriceFormatted = currencyFormatter.format(detail.pizza.unitPrice),
        )

        val toppingsDisplay = detail.availableToppings.map { mapTopping(it) }

        return pizzaDisplay to toppingsDisplay
    }

    fun mapTopping(topping: Topping): ToppingDisplayModel = ToppingDisplayModel(
        id = topping.id,
        name = topping.name,
        unitPrice = topping.unitPrice,
        unitPriceFormatted = currencyFormatter.format(topping.unitPrice),
        imageUrl = topping.imageUrl,
    )

    fun mapToPizzaCartItem(state: PizzaDetailUiState.Ready): CartItem.Pizza {
        val selectedToppings: List<CartTopping> =
            state.toppings.mapNotNull { topping ->
                val qty = state.toppingQuantities[topping.id] ?: 0
                if (qty <= 0) {
                    null
                } else {
                    CartTopping(
                        id = topping.id,
                        name = topping.name,
                        unitPrice = topping.unitPrice,
                        quantity = qty,
                    )
                }
            }

        return CartItem.Pizza(
            lineId = state.lineId ?: UUID.randomUUID().toString(),
            productId = state.pizza.id,
            name = state.pizza.name,
            imageUrl = state.pizza.imageUrl,
            unitPrice = state.pizza.unitPrice,
            toppings = selectedToppings,
            quantity = state.quantity,
            category = ProductCategory.PIZZA,
        )
    }

    fun formatTotalPrice(
        unitPrice: Double,
        toppings: List<ToppingDisplayModel>,
        toppingQuantities: Map<String, Int>,
        quantity: Int,
    ): String {
        val toppingsTotalForOnePizza = toppings.sumOf { topping ->
            val toppingQty = toppingQuantities[topping.id] ?: 0
            topping.unitPrice * toppingQty
        }

        val total = (unitPrice + toppingsTotalForOnePizza) * quantity
        return currencyFormatter.format(total)
    }
}
