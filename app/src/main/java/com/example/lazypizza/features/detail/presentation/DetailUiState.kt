package com.example.lazypizza.features.detail.presentation

import com.example.lazypizza.features.home.domain.models.Product

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val product: Product) : DetailUiState
    data object Error : DetailUiState
}