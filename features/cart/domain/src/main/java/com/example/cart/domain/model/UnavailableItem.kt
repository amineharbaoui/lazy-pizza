package com.example.cart.domain.model

/** An item or topping that couldn't be added to the cart because it is no longer on the menu. */
sealed interface UnavailableItem {
    data class Item(val name: String) : UnavailableItem

    data class Topping(
        val name: String,
        val pizzaName: String,
    ) : UnavailableItem
}
