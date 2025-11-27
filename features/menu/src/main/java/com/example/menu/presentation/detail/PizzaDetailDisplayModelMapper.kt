package com.example.menu.presentation.detail

import com.example.menu.domain.model.PizzaDetail
import com.example.menu.domain.model.Topping
import com.example.ui.utils.formatting.toFormattedCurrency

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