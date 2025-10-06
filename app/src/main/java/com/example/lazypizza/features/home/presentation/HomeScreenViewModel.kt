package com.example.lazypizza.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lazypizza.features.home.domain.usecase.GetHomeSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getHomeSectionsUseCase: GetHomeSectionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    init {
        loadSections()
    }

    private fun loadSections() {
        viewModelScope.launch {
            _uiState.value = HomeScreenUiState.Loading
            runCatching { getHomeSectionsUseCase() }
                .onSuccess { sections ->
                    _uiState.value = HomeScreenUiState.Success(sections)
                }
                .onFailure {
                    _uiState.value = HomeScreenUiState.Error
                }
        }
    }
}
