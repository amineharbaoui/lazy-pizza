package com.example.menu.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menu.domain.model.ProductCategory
import com.example.menu.domain.usecase.ObserveMenuUseCase
import com.example.menu.presentation.model.MenuSectionDisplayModel
import com.example.menu.presentation.model.toDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val observeMenuUseCase: ObserveMenuUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState.onStart {
        observeMenu()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MenuUiState.Loading
    )

    private var allSections: List<MenuSectionDisplayModel> = emptyList()
    private var searchJob: Job? = null

    private fun observeMenu() {
        viewModelScope.launch {
            observeMenuUseCase()
                .onStart { _uiState.value = MenuUiState.Loading }
                .catch { _uiState.value = MenuUiState.Error }
                .map { menuSections -> menuSections.map { it.toDisplayModel() } }
                .collect { menuSectionsDisplayModel ->
                    allSections = menuSectionsDisplayModel

                    val current = _uiState.value
                    val filterTags = if (current is MenuUiState.Success) current.filterTags else emptyList()
                    val searchQuery = if (current is MenuUiState.Success) current.searchQuery else ""

                    val filtered = applyFilters(
                        sections = allSections,
                        categories = filterTags,
                        query = searchQuery
                    )

                    _uiState.value = MenuUiState.Success(
                        originalMenu = allSections,
                        filteredMenu = filtered,
                        filterTags = filterTags,
                        searchQuery = searchQuery
                    )
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        updateSuccessState { current ->
            val newFiltered = applyFilters(
                sections = allSections,
                categories = current.filterTags,
                query = query
            )

            current.copy(
                searchQuery = query,
                filteredMenu = newFiltered
            )
        }
    }

    fun onToggleCategory(category: ProductCategory) {
        updateSuccessState { current ->
            val newFilterTags = current.filterTags.toMutableList().apply {
                if (contains(category)) remove(category) else add(category)
            }

            val newFiltered = applyFilters(
                sections = allSections,
                categories = newFilterTags,
                query = current.searchQuery
            )

            current.copy(
                filterTags = newFilterTags,
                filteredMenu = newFiltered
            )
        }
    }

    private fun applyFilters(
        sections: List<MenuSectionDisplayModel>,
        categories: List<ProductCategory>,
        query: String,
    ): List<MenuSectionDisplayModel> {

        val categoryFiltered =
            if (categories.isEmpty()) sections else sections.filter { it.category in categories }

        return if (query.isBlank()) {
            categoryFiltered
        } else {
            categoryFiltered.mapNotNull { section ->
                val filteredItems = section.items.filter { item ->
                    item.name.contains(query, ignoreCase = true)
                }
                if (filteredItems.isEmpty()) null else section.copy(items = filteredItems)
            }
        }
    }

    private inline fun updateSuccessState(
        block: (MenuUiState.Success) -> MenuUiState.Success,
    ) {
        val current = _uiState.value
        if (current is MenuUiState.Success) {
            _uiState.update { block(current) }
        }
    }
}