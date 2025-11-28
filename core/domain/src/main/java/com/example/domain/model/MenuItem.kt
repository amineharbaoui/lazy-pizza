package com.example.domain.model

sealed interface MenuItem {
    val id: String
    val name: String
    val imageUrl: String
    val price: Double

    data class PizzaItem(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val price: Double,
        val description: String,
        val toppings: List<Topping> = emptyList(),
    ) : MenuItem

    data class OtherMenuItem(
        override val id: String,
        override val name: String,
        override val imageUrl: String,
        override val price: Double,
        val category: ProductCategory,
    ) : MenuItem

}

data class MenuSection(
    val category: ProductCategory,
    val items: List<MenuItem>,
)

data class Topping(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
)


enum class ProductCategory {
    PIZZA,
    DRINK,
    SAUCE,
    ICE_CREAM,
    TOPPING,
}
