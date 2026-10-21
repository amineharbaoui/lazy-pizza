package com.example.cart.data.repository

import com.example.cart.data.datasource.CartLocalDataSource
import com.example.cart.data.datasource.CartTransferLocalDataSource
import com.example.cart.data.mapper.CartOwnerToKeyMapper
import com.example.cart.domain.model.CartOwner
import io.mockk.bdd.coThen
import io.mockk.mockk
import org.junit.jupiter.api.Test

class CartRepositoryImplTest {

    private val localDataSource: CartLocalDataSource = mockk(relaxUnitFun = true)
    private val transferLocalDataSource: CartTransferLocalDataSource = mockk(relaxUnitFun = true)

    private val repository = CartRepositoryImpl(
        localDataSource = localDataSource,
        transferLocalDataSource = transferLocalDataSource,
        ownerToKeyMapper = CartOwnerToKeyMapper(),
    )

    @Test
    suspend fun `transferGuestCart when called then moves guest lines to user key`() {
        // When
        repository.transferGuestCart(to = CartOwner.User(USER_ID))

        // Then
        coThen { transferLocalDataSource.transferCart(fromOwnerKey = GUEST_KEY, toOwnerKey = USER_KEY) }
    }

    @Test
    suspend fun `clear when owner is user then clears user key`() {
        // When
        repository.clear(owner = CartOwner.User(USER_ID))

        // Then
        coThen { localDataSource.clear(ownerKey = USER_KEY) }
    }

    private companion object {
        const val USER_ID = "user-123"
        const val GUEST_KEY = "guest"
        const val USER_KEY = "user:user-123"
    }
}
