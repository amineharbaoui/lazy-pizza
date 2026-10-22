package com.example.menu.data.datasource

import com.example.core.model.ProductCategory
import com.example.menu.data.model.ProductDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Scriptable in-memory menu. Products are grouped by their `category` string, toppings included,
 * so "today's topping list" is the TOPPING products set here.
 */
class FakeProductRemoteDataSource : ProductRemoteDataSource {

    private val products = MutableStateFlow<List<ProductDto>>(emptyList())

    /** When true, every flow throws [FakeMenuLoadException] on collection. */
    var isFailing: Boolean = false

    fun setProducts(vararg items: ProductDto) {
        products.value = items.toList()
    }

    fun setEmptyMenu() {
        products.value = emptyList()
    }

    override fun observeProductsByCategory(category: ProductCategory): Flow<List<ProductDto>> = failingOr {
        products.map { all -> all.filter { it.category == category.name } }
    }

    override fun observeProductById(id: String): Flow<ProductDto?> = failingOr {
        products.map { all -> all.firstOrNull { it.id == id } }
    }

    private fun <T> failingOr(source: () -> Flow<T>): Flow<T> = flow {
        if (isFailing) throw FakeMenuLoadException()
        emitAll(source())
    }
}

class FakeMenuLoadException : Exception("Fake menu load failure")
