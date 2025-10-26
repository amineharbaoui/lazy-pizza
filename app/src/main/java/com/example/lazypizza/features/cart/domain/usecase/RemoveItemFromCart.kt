package com.example.lazypizza.features.cart.domain.usecase

import com.example.lazypizza.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveItemFromCart @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(itemId: String) {
        repository.removeItem(itemId)
    }
}