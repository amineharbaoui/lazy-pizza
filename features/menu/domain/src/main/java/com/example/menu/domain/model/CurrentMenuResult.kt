package com.example.menu.domain.model

/** Today's menu, read once. */
sealed interface CurrentMenuResult {
    data class Loaded(
        val items: List<MenuItem>,
        val toppings: List<Topping>,
    ) : CurrentMenuResult

    /** The menu can't be loaded, for example because of a server error. */
    data object Unavailable : CurrentMenuResult
}
