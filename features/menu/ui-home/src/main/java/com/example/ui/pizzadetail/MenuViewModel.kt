package com.example.ui.pizzadetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.OtherCartItem
import com.example.domain.usecase.AddCartItemUseCase
import com.example.domain.usecase.ObserveCartUseCase
import com.example.domain.usecase.ObserveMenuUseCase
import com.example.domain.usecase.RemoveCartItemUseCase
import com.example.domain.usecase.SignOutUseCase
import com.example.domain.usecase.UpdateCartItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val observeMenuUseCase: ObserveMenuUseCase,
    private val observeCartUseCase: ObserveCartUseCase,
    private val addCartItemUseCase: AddCartItemUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState.onStart {
        observeMenu()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = MenuUiState.Loading,
    )

    private var allSections: List<MenuSectionDisplayModel> = emptyList()

    private fun observeMenu() {
        viewModelScope.launch {
            combine(
                observeMenuUseCase(),
                observeCartUseCase(),
            ) { menuSections, cart ->
                val menuSectionsDisplayModel = menuSections.map { it.toDisplayModel() }

                val qtyByProductId = cart.items
                    .filterIsInstance<OtherCartItem>()
                    .associate { it.productId to it.quantity }

                val menuWithQuantities = menuSectionsDisplayModel.map { section ->
                    section.copy(
                        items = section.items.map { item ->
                            if (item is MenuItemDisplayModel.Other) {
                                val qty = qtyByProductId[item.id] ?: 0
                                item.copy(quantity = qty)
                            } else {
                                item
                            }
                        },
                    )
                }

                allSections = menuWithQuantities

                val current = _uiState.value
                val searchQuery = if (current is MenuUiState.Success) current.searchQuery else ""

                val tags = menuWithQuantities
                    .mapNotNull { it.category.toMenuTag() }
                    .distinct()

                val filtered = applyFilters(
                    sections = menuWithQuantities,
                    query = searchQuery.trim(),
                )

                MenuUiState.Success(
                    originalMenu = menuWithQuantities,
                    filteredMenu = filtered,
                    menuTags = tags,
                    searchQuery = searchQuery,
                )
            }.collect { uiState ->
                _uiState.value = uiState
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val normalized = query.trim()
        updateSuccessState { current ->
            if (current.searchQuery == normalized) return@updateSuccessState current
            val newFiltered = applyFilters(
                sections = allSections,
                query = normalized,
            )
            current.copy(
                searchQuery = normalized,
                filteredMenu = newFiltered,
            )
        }
    }

    fun onOtherItemAddClick(menuItem: MenuItemDisplayModel.Other) {
        viewModelScope.launch {
            addCartItemUseCase(menuItem.toSimpleCartItem(quantity = menuItem.quantity + 1))
        }
    }

    fun onOtherItemQuantityChange(
        menuItem: MenuItemDisplayModel.Other,
        newQuantity: Int,
    ) {
        viewModelScope.launch {
            when {
                newQuantity == 0 -> removeCartItemUseCase(menuItem.id)
                else -> updateCartItemQuantityUseCase(
                    lineId = menuItem.id,
                    quantity = newQuantity,
                )
            }
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

    private inline fun updateSuccessState(block: (MenuUiState.Success) -> MenuUiState.Success) {
        val current = _uiState.value
        if (current is MenuUiState.Success) {
            _uiState.update { block(current) }
        }
    }

    fun onSignOutClick() {
        viewModelScope.launch { signOutUseCase() }
    }
}
