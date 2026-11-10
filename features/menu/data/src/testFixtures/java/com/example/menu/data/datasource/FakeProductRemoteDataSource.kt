package com.example.menu.data.datasource

import com.example.core.model.ProductCategory
import com.example.menu.data.model.ProductDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

internal class FakeProductRemoteDataSource : ProductRemoteDataSource {

    val products = MutableStateFlow<List<ProductDto>>(emptyList())

    var failure: Exception? = null

    override fun observeProductsByCategory(category: ProductCategory): Flow<List<ProductDto>> = products
        .onStart { failure?.let { throw it } }
        .map { all -> all.filter { it.category == category.name } }

    override fun observeProductById(id: String): Flow<ProductDto?> = products
        .onStart { failure?.let { throw it } }
        .map { all -> all.firstOrNull { it.id == id } }
}
