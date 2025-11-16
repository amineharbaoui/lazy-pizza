package com.example.lazypizza.features.home.domain.repository

import com.example.lazypizza.features.home.domain.models.CategorySection
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeHomeSections(): Flow<List<CategorySection>>
}
