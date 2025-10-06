package com.example.lazypizza.features.home.domain.usecase

import com.example.lazypizza.features.home.domain.CategorySection
import com.example.lazypizza.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeSectionsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): List<CategorySection> = repository.getHomeSections()
}
