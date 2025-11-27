package com.example.menu.domain.model

data class PizzaDetail(
    val pizza: MenuItem.PizzaItem,
    val availableToppings: List<Topping>,
)