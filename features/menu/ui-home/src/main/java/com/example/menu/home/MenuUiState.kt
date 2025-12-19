package com.example.menu.home

sealed interface MenuUiState {
    object Loading : MenuUiState
    data class Ready(
        val originalMenu: List<MenuSectionDisplayModel>,
        val filteredMenu: List<MenuSectionDisplayModel>,
        val menuTags: List<MenuTag>,
        val searchQuery: String = "",
    ) : MenuUiState

    object Error : MenuUiState
}
