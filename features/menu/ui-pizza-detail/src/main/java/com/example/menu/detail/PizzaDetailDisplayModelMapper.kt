package com.example.menu.detail

import com.example.domain.model.CartItem
import com.example.domain.model.CartTopping
import com.example.domain.model.PizzaDetail
import com.example.domain.model.Topping
import com.example.menu.utils.formatting.toFormattedCurrency
import com.example.model.ProductCategory
import java.util.UUID

fun PizzaDetail.toDisplayModels(): Pair<PizzaDetailDisplayModel, List<ToppingDisplayModel>> {
    val pizzaDisplay = PizzaDetailDisplayModel(
        id = pizza.id,
        name = pizza.name,
        description = pizza.description,
        imageUrl = pizza.imageUrl,
        unitPrice = pizza.unitPrice,
        unitPriceFormatted = pizza.unitPrice.toFormattedCurrency(),
    )

    val toppingsDisplay = availableToppings.map { it.toDisplayModel() }

    return pizzaDisplay to toppingsDisplay
}

fun Topping.toDisplayModel() = ToppingDisplayModel(
    id = id,
    name = name,
    unitPrice = unitPrice,
    unitPriceFormatted = unitPrice.toFormattedCurrency(),
    imageUrl = imageUrl,
)

fun PizzaDetailUiState.Ready.toPizzaCartItem(): CartItem.Pizza {
    val selectedToppings: List<CartTopping> =
        toppings.mapNotNull { topping ->
            val qty = toppingQuantities[topping.id] ?: 0
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
        lineId = UUID.randomUUID().toString(),
        productId = pizza.id,
        name = pizza.name,
        imageUrl = pizza.imageUrl,
        unitPrice = pizza.unitPrice,
        toppings = selectedToppings,
        quantity = quantity,
        category = ProductCategory.PIZZA,
    )
}
