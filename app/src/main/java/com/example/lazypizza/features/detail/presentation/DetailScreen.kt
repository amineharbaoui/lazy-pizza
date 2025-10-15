package com.example.lazypizza.features.detail.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DetailScreen(
    innerPadding: PaddingValues,
    viewModel: DetailScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when(val state = uiState) {
        is DetailUiState.Loading -> {

        }
        is DetailUiState.Success -> {
            println("Success state: ${state.product}")
        }
        is DetailUiState.Error -> {
            println("Error state: ${state}")
        }
    }
}