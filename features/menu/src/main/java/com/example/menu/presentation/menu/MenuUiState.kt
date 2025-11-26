package com.example.menu.presentation.menu

import com.example.menu.presentation.model.MenuSectionDisplayModel
import com.example.menu.presentation.model.MenuTag

sealed interface MenuUiState {
    object Loading : MenuUiState
    data class Success(
        val originalMenu: List<MenuSectionDisplayModel>,
        val filteredMenu: List<MenuSectionDisplayModel>,
        val menuTags: List<MenuTag>,
        val searchQuery: String = "",
    ) : MenuUiState

    object Error : MenuUiState
}