package com.example.lazypizza.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lazypizza.features.home.domain.CategorySection
import com.example.lazypizza.features.home.domain.usecase.GetHomeSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getHomeSectionsUseCase: GetHomeSectionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState
        .onStart { loadSections() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = HomeScreenUiState.Loading
        )

    private var searchJob: Job? = null

    private fun loadSections() {
        viewModelScope.launch {
            runCatching { getHomeSectionsUseCase() }
                .onSuccess { sections ->
                    _uiState.value = HomeScreenUiState.Success(
                        originalSections = sections,
                        displaySections = sections,
                        searchQuery = ""
                    )
                }
                .onFailure {
                    _uiState.value = HomeScreenUiState.Error
                }
        }
    }

    fun onSearchQueryChange(newSearchQuery: String) {
        searchJob?.cancel()
        val currentState = _uiState.value
        if (currentState is HomeScreenUiState.Success) {
            _uiState.value = currentState.copy(searchQuery = newSearchQuery)
        }
        searchJob = viewModelScope.launch {
            delay(300.milliseconds)

            val updatedState = _uiState.value
            if (updatedState is HomeScreenUiState.Success) {
                val filteredList = filterSections(
                    sections = updatedState.originalSections,
                    query = updatedState.searchQuery
                )
                _uiState.value = updatedState.copy(displaySections = filteredList)
            }
        }
    }

    private fun filterSections(
        sections: List<CategorySection>,
        query: String
    ) = sections.mapNotNull { section ->
        val filteredProducts = section.products.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
        }
        if (filteredProducts.isNotEmpty()) {
            section.copy(products = filteredProducts)
        } else {
            null
        }
    }
}
