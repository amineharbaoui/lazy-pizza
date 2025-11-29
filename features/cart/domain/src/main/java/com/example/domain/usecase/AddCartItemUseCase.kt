package com.example.domain.usecase

import com.example.domain.model.CartItem
import com.example.domain.repository.CartRepository

class AddCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(item: CartItem) {
        println(item.lineId)
        repository.addItem(item)
    }
}
