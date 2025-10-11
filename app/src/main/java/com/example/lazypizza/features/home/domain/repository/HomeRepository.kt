package com.example.lazypizza.features.home.domain.repository

import com.example.lazypizza.features.home.domain.models.CategorySection
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun observeHomeSections(): Flow<List<CategorySection>>
}
