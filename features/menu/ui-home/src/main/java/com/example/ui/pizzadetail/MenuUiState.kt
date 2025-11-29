package com.example.ui.pizzadetail

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
