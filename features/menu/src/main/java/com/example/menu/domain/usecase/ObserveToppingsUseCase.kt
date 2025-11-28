package com.example.menu.domain.usecase

import com.example.domain.model.Topping
import com.example.menu.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveToppingsUseCase @Inject constructor(
    private val repository: MenuRepository,
) {
    operator fun invoke(): Flow<List<Topping>> = repository.observeToppings()
}