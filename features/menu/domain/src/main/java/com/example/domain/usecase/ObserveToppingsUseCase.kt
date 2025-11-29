package com.example.domain.usecase

import com.example.domain.model.Topping
import com.example.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow

class ObserveToppingsUseCase(private val repository: MenuRepository) {
    operator fun invoke(): Flow<List<Topping>> = repository.observeToppings()
}
