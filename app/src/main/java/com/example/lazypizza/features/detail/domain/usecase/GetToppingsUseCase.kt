package com.example.lazypizza.features.detail.domain.usecase

import com.example.lazypizza.features.detail.domain.repository.DetailRepository
import com.example.lazypizza.features.home.domain.models.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetToppingsUseCase @Inject constructor(private val detailRepository: DetailRepository) {
    operator fun invoke(): Flow<List<Product>> = detailRepository.observeToppings()
}