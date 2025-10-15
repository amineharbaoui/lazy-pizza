package com.example.lazypizza.features.detail.domain.usecase

import com.example.lazypizza.features.detail.domain.repository.DetailRepository
import com.example.lazypizza.features.home.domain.models.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(private val detailRepository: DetailRepository) {
    suspend operator fun invoke(productId: String): Flow<Product?> =
        detailRepository.observeProductById(productId)
}