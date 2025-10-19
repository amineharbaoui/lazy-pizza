package com.example.lazypizza.features.detail.domain.repository

import com.example.lazypizza.features.home.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun observeProductById(productId: String): Flow<Product?>
    fun observeToppings(): Flow<List<Product>>
}