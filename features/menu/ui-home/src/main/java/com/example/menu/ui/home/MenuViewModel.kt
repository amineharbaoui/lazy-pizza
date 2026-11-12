package com.example.menu.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.ObserveIsSignedInUseCase
import com.example.auth.domain.usecase.SignOutUseCase
import com.example.cart.domain.usecase.AddCartItemUseCase
import com.example.cart.domain.usecase.ClearCartUseCase
import com.example.cart.domain.usecase.ObserveCartUseCase
import com.example.cart.domain.usecase.RemoveCartItemUseCase
import com.example.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.menu.domain.usecase.ObserveMenuUseCase
import com.example.menu.ui.home.mapper.MenuDomainToUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MenuViewModel @Inject constructor(
    observeMenuUseCase: ObserveMenuUseCase,
    observeCartUseCase: ObserveCartUseCase,
    private val addCartItemUseCase: AddCartItemUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val signOutUseCase: SignOutUseCase,
    observeIsSignedInUseCase: ObserveIsSignedInUseCase,
    private val menuMapper: MenuDomainToUiMapper,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<MenuUiState> = combine(
        observeMenuUseCase(),
        observeCartUseCase(),
        observeIsSignedInUseCase(),
        searchQuery,
    ) { menuSections, cart, isSignedIn, query ->
        MenuUiState(
            isLoggedIn = isSignedIn,
            content = menuMapper.mapToMenuContentUiState(menuSections, cart, query),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = MenuUiState(isLoggedIn = false, content = MenuContentUiState.Loading),
    )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query.trim()
    }

    fun addOtherItemToCart(menuItem: MenuItemDisplayModel.Other) {
        viewModelScope.launch {
            addCartItemUseCase(menuMapper.mapToSimpleCartItem(menuItem, quantity = menuItem.quantity + 1))
        }
    }

    fun updateOtherItemQuantity(
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

    fun signOut() {
        viewModelScope.launch {
            clearCartUseCase()
            signOutUseCase()
        }
    }
}
