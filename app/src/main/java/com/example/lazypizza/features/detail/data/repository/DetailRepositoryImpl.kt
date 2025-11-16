package com.example.lazypizza.features.detail.data.repository

import com.example.core.firebase.firestore.datasource.ProductDatasource
import com.example.lazypizza.data.mapper.toDomain
import com.example.lazypizza.features.detail.domain.repository.DetailRepository
import com.example.lazypizza.features.home.domain.models.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val productDatasource: ProductDatasource,
) : DetailRepository {
    override fun observeProductById(productId: String): Flow<Product?> =
        productDatasource.observeProductById(productId).map { it?.toDomain() }

    override fun observeToppings(): Flow<List<Product>> =
        productDatasource.observeProductsInCategory("TOPPINGS").map { list ->
            list.map { it.toDomain() }
        }
}