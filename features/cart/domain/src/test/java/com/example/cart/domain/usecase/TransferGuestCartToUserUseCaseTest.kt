package com.example.cart.domain.usecase

import com.example.cart.domain.repository.CartRepository
import io.mockk.bdd.coThen
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class TransferGuestCartToUserUseCaseTest {

    @MockK(relaxUnitFun = true)
    lateinit var repository: CartRepository

    @InjectMockKs
    lateinit var transferGuestCartToUserUseCase: TransferGuestCartToUserUseCase

    @Test
    suspend fun `invoke when called with user id then transfers guest cart to that user`() {
        // When
        transferGuestCartToUserUseCase(USER_ID)

        // Then
        coThen { repository.transferGuestCart(to = USER_OWNER) }
    }
}
