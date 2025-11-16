package com.example.lazypizza.features.home.presentation

import com.example.lazypizza.features.home.domain.models.ProductCategory

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Success(
        val originalSections: List<CategorySectionDisplayModel>,
        val displaySections: List<CategorySectionDisplayModel>,
        val filterTags: List<String>,
        val searchQuery: String = "",
    ) : HomeScreenUiState

    data object Error : HomeScreenUiState
}

data class ProductDisplayModel(
    val id: String,
    val category: ProductCategory,
    val name: String,
    val description: String,
    val price: Double,
    val priceFormatted: String,
    val imageUrl: String,
)

data class CategorySectionDisplayModel(
    val category: ProductCategory,
    val products: List<ProductDisplayModel>,
)