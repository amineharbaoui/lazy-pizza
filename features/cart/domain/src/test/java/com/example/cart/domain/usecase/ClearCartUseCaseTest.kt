package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.repository.CartRepository
import io.mockk.bdd.coGiven
import io.mockk.bdd.coThen
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ClearCartUseCaseTest {

    @MockK(relaxUnitFun = true)
    lateinit var repository: CartRepository

    @MockK
    lateinit var sessionRepository: SessionRepository

    @InjectMockKs
    lateinit var clearCartUseCase: ClearCartUseCase

    @Test
    suspend fun `invoke when user is signed in then clears user cart`() {
        // Given
        coGiven { sessionRepository.currentUserUid() } returns USER_ID

        // When
        clearCartUseCase()

        // Then
        coThen { repository.clear(owner = USER_OWNER) }
    }
}
