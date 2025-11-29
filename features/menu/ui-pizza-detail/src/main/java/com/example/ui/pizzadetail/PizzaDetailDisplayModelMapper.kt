package com.example.ui.pizzadetail

import com.example.domain.model.CartTopping
import com.example.domain.model.PizzaCartItem
import com.example.domain.model.PizzaDetail
import com.example.domain.model.Topping
import com.example.ui.utils.formatting.toFormattedCurrency
import java.util.UUID

fun PizzaDetail.toDisplayModels(): Pair<PizzaDetailDisplayModel, List<ToppingDisplayModel>> {
    val pizzaDisplay = PizzaDetailDisplayModel(
        id = pizza.id,
        name = pizza.name,
        description = pizza.description,
        imageUrl = pizza.imageUrl,
        price = pizza.price,
        priceFormatted = pizza.price.toFormattedCurrency(),
    )

    val toppingsDisplay = availableToppings.map { it.toDisplayModel() }

    return pizzaDisplay to toppingsDisplay
}

fun Topping.toDisplayModel() = ToppingDisplayModel(
    id = id,
    name = name,
    price = price,
    priceFormatted = price.toFormattedCurrency(),
    imageUrl = imageUrl,
)

fun PizzaDetailUiState.Success.toPizzaCartItem(): PizzaCartItem {
    val selectedToppings: List<CartTopping> =
        toppings.mapNotNull { topping ->
            val qty = toppingQuantities[topping.id] ?: 0
            if (qty <= 0) {
                null
            } else {
                CartTopping(
                    toppingId = topping.id,
                    name = topping.name,
                    price = topping.price,
                    quantity = qty,
                )
            }
        }

    return PizzaCartItem(
        lineId = UUID.randomUUID().toString(),
        productId = pizza.id,
        name = pizza.name,
        imageUrl = pizza.imageUrl,
        basePrice = pizza.price,
        toppings = selectedToppings,
        quantity = quantity,
    )
}
