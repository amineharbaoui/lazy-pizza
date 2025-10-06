package com.example.lazypizza.features.home.presentation

import com.example.lazypizza.features.home.domain.CategorySection

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Success(val sections: List<CategorySection>) : HomeScreenUiState
    data object Error : HomeScreenUiState
}