package com.example.domain.model

data class PizzaDetail(
    val pizza: MenuItem.PizzaItem,
    val availableToppings: List<Topping>,
)
