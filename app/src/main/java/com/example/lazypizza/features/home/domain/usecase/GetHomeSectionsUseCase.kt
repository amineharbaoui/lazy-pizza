package com.example.lazypizza.features.home.domain.usecase

import com.example.lazypizza.features.home.domain.models.CategorySection
import com.example.lazypizza.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHomeSectionsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<List<CategorySection>> = repository.observeHomeSections()
}
