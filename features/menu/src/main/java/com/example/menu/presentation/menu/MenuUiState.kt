package com.example.menu.presentation.menu

import com.example.menu.domain.model.ProductCategory
import com.example.menu.presentation.model.MenuSectionDisplayModel

sealed interface MenuUiState {
    object Loading : MenuUiState
    data class Success(
        val originalMenu: List<MenuSectionDisplayModel>,
        val filteredMenu: List<MenuSectionDisplayModel>,
        val filterTags: List<ProductCategory>,
        val searchQuery: String = "",
    ) : MenuUiState

    object Error : MenuUiState
}