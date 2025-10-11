package com.example.lazypizza.features.home.data.repository

import com.example.lazypizza.features.home.data.datasource.RemoteDataSource
import com.example.lazypizza.features.home.data.mapper.toDomain
import com.example.lazypizza.features.home.data.mapper.toSections
import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
) : HomeRepository {

    override suspend fun observeHomeSections(): Flow<List<CategorySection>> =
        remoteDataSource.observeSections().map { list ->
            list.map { it.toDomain() }.toSections()
        }
}

