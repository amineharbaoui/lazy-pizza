package com.example.domaine.usecase

import com.example.domaine.model.CartItem
import com.example.domaine.repository.CartRepository
import javax.inject.Inject

class AddCartItemUseCase @Inject constructor(private val repository: CartRepository) {
    suspend operator fun invoke(item: CartItem) {
        repository.addItem(item)
    }
}
