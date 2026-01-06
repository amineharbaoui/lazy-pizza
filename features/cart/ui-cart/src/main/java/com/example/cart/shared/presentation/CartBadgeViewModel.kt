package com.example.cart.shared.presentation

import androidx.lifecycle.ViewModel
import com.example.domain.usecase.ObserveCartItemCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartBadgeViewModel @Inject constructor(
    observeCartItemCountUseCase: ObserveCartItemCountUseCase,
) : ViewModel() {


}
