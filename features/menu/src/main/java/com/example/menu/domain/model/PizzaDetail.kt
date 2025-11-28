package com.example.menu.domain.model

import com.example.domain.model.MenuItem
import com.example.domain.model.Topping

data class PizzaDetail(
    val pizza: MenuItem.PizzaItem,
    val availableToppings: List<Topping>,
)