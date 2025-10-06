package com.example.lazypizza.features.home.domain.repository

import com.example.lazypizza.features.home.domain.CategorySection

interface HomeRepository {
    suspend fun getHomeSections(): List<CategorySection>
}
