package com.example.domain.model

data class CartTopping(
    val toppingId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
)

sealed interface CartItem {
    val lineId: String
    val productId: String
    val name: String
    val imageUrl: String
    val quantity: Int
    val lineTotal: Double

    data class Other(
        override val lineId: String,
        override val productId: String,
        override val name: String,
        override val imageUrl: String,
        val price: Double,
        override val quantity: Int,
    ) : CartItem {
        override val lineTotal: Double
            get() = price * quantity
    }

    data class Pizza(
        override val lineId: String,
        override val productId: String,
        override val name: String,
        override val imageUrl: String,
        val basePrice: Double,
        val toppings: List<CartTopping>,
        override val quantity: Int,
    ) : CartItem {

        private val toppingsPriceForOne: Double
            get() = toppings.sumOf { it.price * it.quantity }

        override val lineTotal: Double
            get() = (basePrice + toppingsPriceForOne) * quantity
    }
}

data class Cart(
    val items: List<CartItem> = emptyList(),
) {
    val subtotal: Double
        get() = items.sumOf { it.lineTotal }
}
