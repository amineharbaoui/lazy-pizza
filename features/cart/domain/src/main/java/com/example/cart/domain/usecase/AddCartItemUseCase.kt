package com.example.cart.domain.usecase

import com.example.cart.domain.model.CartItem
import com.example.cart.domain.repository.CartRepository
import javax.inject.Inject

class AddCartItemUseCase @Inject constructor(private val repository: CartRepository) {
    suspend operator fun invoke(item: CartItem) {
        repository.addItem(item)
    }
}
