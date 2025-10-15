package com.example.lazypizza.features.detail.data.repository

import com.example.lazypizza.features.detail.domain.repository.DetailRepository
import com.example.lazypizza.features.home.data.datasource.RemoteDataSource
import com.example.lazypizza.features.home.data.mapper.toDomain
import com.example.lazypizza.features.home.domain.models.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val remote: RemoteDataSource,
) : DetailRepository {
    override suspend fun observeProductById(productId: String): Flow<Product?> =
        remote.observeProductById(productId).map { it?.toDomain() }
}