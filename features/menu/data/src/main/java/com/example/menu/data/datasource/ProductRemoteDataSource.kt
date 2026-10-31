package com.example.menu.data.datasource

import com.example.core.model.ProductCategory
import com.example.menu.data.model.ProductDto
import kotlinx.coroutines.flow.Flow

interface ProductRemoteDataSource {
    fun observeProductsByCategory(category: ProductCategory): Flow<List<ProductDto>>
    fun observeProductById(id: String): Flow<ProductDto?>
}
