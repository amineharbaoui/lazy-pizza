package com.example.cart.domain.model

sealed interface UnavailableItem {
    data class Item(val name: String) : UnavailableItem

    data class Topping(
        val name: String,
        val pizzaName: String,
    ) : UnavailableItem
}
