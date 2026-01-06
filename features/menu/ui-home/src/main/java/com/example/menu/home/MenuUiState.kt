package com.example.menu.home

data class MenuUiState(
    val isLoggedIn: Boolean,
    val content: MenuContentUiState,
)

sealed interface MenuContentUiState {
    object Loading : MenuContentUiState
    data class Ready(
        val originalMenu: List<MenuSectionDisplayModel>,
        val filteredMenu: List<MenuSectionDisplayModel>,
        val menuTags: List<MenuTag>,
        val searchQuery: String = "",
    ) : MenuContentUiState

    object Error : MenuContentUiState
}
