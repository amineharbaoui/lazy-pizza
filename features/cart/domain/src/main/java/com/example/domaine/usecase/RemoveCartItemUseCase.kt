package com.example.domaine.usecase

import com.example.domaine.repository.CartRepository
import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(private val repository: CartRepository) {
    suspend operator fun invoke(lineId: String) {
        repository.removeItem(lineId)
    }
}
