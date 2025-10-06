package com.example.lazypizza.features.home.data.repository

import com.example.lazypizza.features.home.data.datasource.HomeLocalDataSource
import com.example.lazypizza.features.home.domain.CategorySection
import com.example.lazypizza.features.home.domain.ProductCategory
import com.example.lazypizza.features.home.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val localDataSource: HomeLocalDataSource
) : HomeRepository {

    override suspend fun getHomeSections(): List<CategorySection> {
        val products = localDataSource.getProducts()
        return ProductCategory.entries.mapNotNull { category ->
            val itemsForCategory = products.filter { it.category == category }
            if (itemsForCategory.isEmpty()) return@mapNotNull null
            CategorySection(
                category = category,
                products = itemsForCategory
            )
        }
    }
}
