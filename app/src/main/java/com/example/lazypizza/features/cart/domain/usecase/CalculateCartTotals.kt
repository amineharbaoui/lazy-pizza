package com.example.lazypizza.features.cart.domain.usecase

import com.example.lazypizza.features.cart.domain.model.Cart
import com.example.lazypizza.features.cart.domain.model.CartTotals
import javax.inject.Inject

class CalculateCartTotals @Inject constructor() {
    operator fun invoke(cart: Cart): CartTotals {
        val subtotal = cart.items.sumOf { item ->
            val toppingsSum = item.toppings.sumOf { it.unitPrice * it.quantity }
            item.basePrice + toppingsSum
        }
        return CartTotals(subtotal = subtotal, total = subtotal)
    }
}