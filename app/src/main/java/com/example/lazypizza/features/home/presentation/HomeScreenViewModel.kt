package com.example.lazypizza.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.firebase.firestore.datasource.ProductDatasource
import com.example.lazypizza.features.home.domain.usecase.GetHomeSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getHomeSectionsUseCase: GetHomeSectionsUseCase,
    private val productDatasource: ProductDatasource,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.onStart {
        observeSections()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = HomeScreenUiState.Loading
    )

    private var searchJob: Job? = null

    private fun observeSections() {
//        viewModelScope.launch {
//            getHomeSectionsUseCase()
//                .onStart { _uiState.value = HomeScreenUiState.Loading }
//                .catch { _uiState.value = HomeScreenUiState.Error }
//                .collect { sections ->
//                    val currentQuery = (uiState.value as? HomeScreenUiState.Success)
//                        ?.searchQuery
//                        .orEmpty()
//
//                    val mapped = sections.toDisplayModels()
//                    val display = if (currentQuery.isBlank()) {
//                        mapped
//                    } else {
//                        filterSections(mapped, currentQuery)
//                    }
//                    _uiState.value = HomeScreenUiState.Success(
//                        originalSections = mapped,
//                        displaySections = display,
//                        filterTags = ProductCategory.entries
//                            .filterNot { it == ProductCategory.TOPPINGS }
//                            .map { it.value },
//                        searchQuery = currentQuery
//                    )
//                }
//        }
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
        sections: List<CategorySectionDisplayModel>,
        query: String,
    ) = sections.mapNotNull { section ->
        val filteredProducts = section.products.filter { product ->
            product.name.contains(query, ignoreCase = true) || product.description.contains(query, ignoreCase = true)
        }
        if (filteredProducts.isNotEmpty()) {
            section.copy(products = filteredProducts)
        } else {
            null
        }
    }
}
