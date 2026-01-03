package com.example.domain.usecase

import com.example.domain.model.PizzaDetail
import com.example.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObservePizzaDetailUseCase @Inject constructor(private val menuRepository: MenuRepository) {
    operator fun invoke(id: String): Flow<PizzaDetail> {
        return combine(
            menuRepository.observePizzaById(id),
            menuRepository.observeToppings(),
        ) { pizza, toppings ->
            val pizzaDetails = pizza ?: throw IllegalStateException("Pizza with id=$id not found")
            PizzaDetail(
                pizza = pizzaDetails,
                availableToppings = toppings,
            )
        }
    }
}
