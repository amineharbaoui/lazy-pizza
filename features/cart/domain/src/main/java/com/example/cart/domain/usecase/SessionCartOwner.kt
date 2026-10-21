package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.CartOwner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

// Business rule: a signed-in user owns a session-scoped cart, everyone else uses the guest cart.
internal fun SessionRepository.observeCartOwner(): Flow<CartOwner> = userIdFlow
    .map(::cartOwnerOf)
    .distinctUntilChanged()

internal suspend fun SessionRepository.currentCartOwner(): CartOwner = cartOwnerOf(currentUserUid())

private fun cartOwnerOf(userId: String?): CartOwner = userId?.let(CartOwner::User) ?: CartOwner.Guest
