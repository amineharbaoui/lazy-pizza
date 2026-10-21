package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.repository.CartRepository
import io.mockk.bdd.coGiven
import io.mockk.bdd.coThen
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RemoveCartItemUseCaseTest {

    @MockK(relaxUnitFun = true)
    lateinit var repository: CartRepository

    @MockK
    lateinit var sessionRepository: SessionRepository

    @InjectMockKs
    lateinit var removeCartItemUseCase: RemoveCartItemUseCase

    @Test
    suspend fun `invoke when user is signed out then removes item from guest cart`() {
        // Given
        coGiven { sessionRepository.currentUserUid() } returns null

        // When
        removeCartItemUseCase(LINE_ID)

        // Then
        coThen { repository.removeItem(owner = CartOwner.Guest, lineId = LINE_ID) }
    }
}
