package com.example.menu.domain.model

sealed interface CurrentMenuResult {
    data class Loaded(
        val items: List<MenuItem>,
        val toppings: List<Topping>,
    ) : CurrentMenuResult

    data object Unavailable : CurrentMenuResult
}
