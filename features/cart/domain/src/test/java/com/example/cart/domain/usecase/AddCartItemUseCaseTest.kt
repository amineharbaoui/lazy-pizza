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
class AddCartItemUseCaseTest {

    @MockK(relaxUnitFun = true)
    lateinit var repository: CartRepository

    @MockK
    lateinit var sessionRepository: SessionRepository

    @InjectMockKs
    lateinit var addCartItemUseCase: AddCartItemUseCase

    private val item = cartItem()

    @Test
    suspend fun `invoke when user is signed out then adds item to guest cart`() {
        // Given
        coGiven { sessionRepository.currentUserUid() } returns null

        // When
        addCartItemUseCase(item)

        // Then
        coThen { repository.addItem(owner = CartOwner.Guest, item = item) }
    }

    @Test
    suspend fun `invoke when user is signed in then adds item to user cart`() {
        // Given
        coGiven { sessionRepository.currentUserUid() } returns USER_ID

        // When
        addCartItemUseCase(item)

        // Then
        coThen { repository.addItem(owner = USER_OWNER, item = item) }
    }
}
