package com.example.menu.domain.usecase

import com.example.domain.model.MenuItem
import com.example.menu.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePizzaByIdUseCase @Inject constructor(
    private val repository: MenuRepository
) {
    operator fun invoke(id: String): Flow<MenuItem.PizzaItem?> = repository.observePizzaById(id)
}