package com.example.lazypizza.features.home.presentation

import com.example.lazypizza.features.home.domain.CategorySection

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Success(
        val originalSections: List<CategorySection>,
        val displaySections: List<CategorySection>,
        val searchQuery: String = ""
    ) : HomeScreenUiState
    data object Error : HomeScreenUiState
}