package com.example.menu.data.repository

import com.example.core.model.ProductCategory
import com.example.menu.data.datasource.ProductRemoteDataSource
import com.example.menu.data.mapper.ProductDtoToDomainMapper
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.repository.RecommendedItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class RecommendedItemsRepositoryImpl @Inject constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productMapper: ProductDtoToDomainMapper,
) : RecommendedItemsRepository {
    override fun getRecommendedItems(): Flow<List<MenuItem>> = combine(
        productRemoteDataSource.observeProductsByCategory(ProductCategory.DRINK),
        productRemoteDataSource.observeProductsByCategory(ProductCategory.ICE_CREAM),
    ) { drinks, iceCreams ->
        val products = productMapper.mapListToMenuItems(drinks) + productMapper.mapListToMenuItems(iceCreams)
        products.shuffled().take(10)
    }
}
