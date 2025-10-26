package com.example.lazypizza.features.cart.domain.usecase

import com.example.lazypizza.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class UpdateItemToppingQuantity @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(itemId: String, toppingId: String, quantity: Int) {
        repository.updateItemToppingQuantity(itemId, toppingId, quantity)
    }
}