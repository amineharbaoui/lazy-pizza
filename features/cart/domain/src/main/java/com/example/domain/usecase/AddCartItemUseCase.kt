package com.example.domain.usecase

import com.example.domain.model.CartItem
import com.example.domain.repository.CartRepository
import javax.inject.Inject

class AddCartItemUseCase @Inject constructor(private val repository: CartRepository) {
    suspend operator fun invoke(item: CartItem) {
        repository.addItem(item)
    }
}
