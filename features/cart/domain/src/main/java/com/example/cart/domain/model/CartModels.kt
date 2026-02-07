package com.example.cart.domain.model

import com.example.core.model.ProductCategory

data class CartTopping(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
)

sealed interface CartItem {
    val lineId: String
    val productId: String
    val name: String
    val imageUrl: String
    val quantity: Int
    val lineTotal: Double
    val category: ProductCategory

    data class Other(
        override val lineId: String,
        override val productId: String,
        override val name: String,
        override val imageUrl: String,
        override val quantity: Int,
        override val category: ProductCategory,
        val unitPrice: Double,
    ) : CartItem {
        override val lineTotal: Double
            get() = unitPrice * quantity
    }

    data class Pizza(
        override val lineId: String,
        override val productId: String,
        override val name: String,
        override val imageUrl: String,
        override val quantity: Int,
        override val category: ProductCategory,
        val unitPrice: Double,
        val toppings: List<CartTopping>,
    ) : CartItem {

        private val toppingsPriceForOne: Double
            get() = toppings.sumOf { it.unitPrice * it.quantity }

        override val lineTotal: Double
            get() = (unitPrice + toppingsPriceForOne) * quantity
    }
}

data class Cart(
    val items: List<CartItem> = emptyList(),
) {
    val subtotal: Double
        get() = items.sumOf { it.lineTotal }
}
