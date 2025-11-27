package com.example.menu.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menu.domain.usecase.ObserveMenuUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val observeMenuUseCase: ObserveMenuUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState.onStart {
        observeMenu()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = MenuUiState.Loading
    )

    private var allSections: List<MenuSectionDisplayModel> = emptyList()

    private fun observeMenu() {
        viewModelScope.launch {
            observeMenuUseCase()
                .onStart { _uiState.value = MenuUiState.Loading }
                .catch { _uiState.value = MenuUiState.Error }
                .map { menuSections -> menuSections.map { it.toDisplayModel() } }
                .collect { menuSectionsDisplayModel ->
                    allSections = menuSectionsDisplayModel

                    val current = _uiState.value
                    val tags = menuSectionsDisplayModel
                        .mapNotNull { it.category.toMenuTag() }
                        .distinct()
                    val searchQuery = if (current is MenuUiState.Success) current.searchQuery else ""

                    val filtered = applyFilters(
                        sections = allSections,
                        query = searchQuery.trim(),
                    )

                    _uiState.value = MenuUiState.Success(
                        originalMenu = allSections,
                        filteredMenu = filtered,
                        menuTags = tags,
                        searchQuery = searchQuery
                    )
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        val normalized = query.trim()
        updateSuccessState { current ->
            if (current.searchQuery == normalized) return@updateSuccessState current
            val newFiltered = applyFilters(
                sections = allSections,
                query = normalized
            )
            current.copy(
                searchQuery = normalized,
                filteredMenu = newFiltered
            )
        }
    }

    private fun applyFilters(
        sections: List<MenuSectionDisplayModel>,
        query: String,
    ): List<MenuSectionDisplayModel> {
        if (query.isBlank()) return sections
        return sections.mapNotNull { section ->
            val filteredItems = section.items.filter { item ->
                item.name.contains(query, ignoreCase = true)
            }
            if (filteredItems.isEmpty()) null else section.copy(items = filteredItems)
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