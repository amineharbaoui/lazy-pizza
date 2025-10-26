package com.example.lazypizza.features.cart.domain.usecase

import com.example.lazypizza.features.cart.domain.model.CartItem
import com.example.lazypizza.features.cart.domain.model.CartToppingSelection
import com.example.lazypizza.features.cart.domain.repository.CartRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Adds a configured pizza to the cart.
 */
class AddItemToCart @Inject constructor(
    private val repository: CartRepository,
) {
    /**
     * @param productId the base product id
     * @param basePrice the base price of the pizza
     * @param toppings list of selected toppings with unit prices and quantities
     */
    suspend operator fun invoke(
        productId: String,
        basePrice: Double,
        toppings: List<CartToppingSelection>,
    ) {
        val item = CartItem(
            id = UUID.randomUUID().toString(),
            productId = productId,
            basePrice = basePrice,
            toppings = toppings,
        )
        repository.addItem(item)
    }
}