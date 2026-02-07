package com.example.menu.data.repository

import com.example.domain.model.MenuItem
import com.example.domain.repository.RecommendedItemsRepository
import com.example.menu.data.datasource.ProductRemoteDataSource
import com.example.menu.data.mapper.ProductDtoToDomainMapper
import com.example.model.ProductCategory
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
