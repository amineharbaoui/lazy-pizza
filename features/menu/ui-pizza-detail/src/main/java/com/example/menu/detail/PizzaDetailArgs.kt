package com.example.menu.detail

sealed interface PizzaDetailArgs {
    val productId: String

    data class Create(override val productId: String) : PizzaDetailArgs
    data class Edit(
        override val productId: String,
        val lineId: String,
    ) : PizzaDetailArgs
}
