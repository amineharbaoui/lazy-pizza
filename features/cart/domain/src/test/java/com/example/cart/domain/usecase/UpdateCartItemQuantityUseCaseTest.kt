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
class UpdateCartItemQuantityUseCaseTest {

    @MockK(relaxUnitFun = true)
    lateinit var repository: CartRepository

    @MockK
    lateinit var sessionRepository: SessionRepository

    @InjectMockKs
    lateinit var updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase

    @Test
    suspend fun `invoke when user is signed in then updates quantity in user cart`() {
        // Given
        coGiven { sessionRepository.currentUserUid() } returns USER_ID

        // When
        updateCartItemQuantityUseCase(lineId = LINE_ID, quantity = NEW_QUANTITY)

        // Then
        coThen { repository.updateItemQuantity(owner = USER_OWNER, lineId = LINE_ID, quantity = NEW_QUANTITY) }
    }

    private companion object {
        const val NEW_QUANTITY = 3
    }
}
