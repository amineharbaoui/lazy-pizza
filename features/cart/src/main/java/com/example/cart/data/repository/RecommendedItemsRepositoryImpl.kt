package com.example.cart.data.repository

import com.example.cart.domain.repository.RecommendedItemsRepository
import com.example.data.datasource.ProductRemoteDataSource
import com.example.data.mapper.toMenuItem
import com.example.domain.model.MenuItem
import com.example.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class RecommendedItemsRepositoryImpl @Inject constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : RecommendedItemsRepository {
    override suspend fun getRecommendedItems(): Flow<List<MenuItem>> =
        combine(
            productRemoteDataSource.observeProductsByCategory(ProductCategory.DRINK),
            productRemoteDataSource.observeProductsByCategory(ProductCategory.ICE_CREAM)
        ) { drinks, iceCreams ->
            val products = drinks.map { it.toMenuItem() } + iceCreams.map { it.toMenuItem() }
            products.shuffled().take(10)
        }


}