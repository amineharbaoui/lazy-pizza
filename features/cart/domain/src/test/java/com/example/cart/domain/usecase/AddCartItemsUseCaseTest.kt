package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.CartWriteResult
import com.example.cart.domain.repository.CartRepository
import io.mockk.bdd.coGiven
import io.mockk.bdd.given
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.IOException

@ExtendWith(MockKExtension::class)
class AddCartItemsUseCaseTest {

    @MockK
    lateinit var repository: CartRepository

    @MockK
    lateinit var sessionRepository: SessionRepository

    @InjectMockKs
    lateinit var addCartItemsUseCase: AddCartItemsUseCase

    @Test
    suspend fun `invoke when reading the cart fails then returns failed`() {
        // Given
        coGiven { sessionRepository.currentUserUid() } returns USER_ID
        given { repository.observeCart(USER_OWNER) } returns flow { throw IOException() }

        // When
        val result = addCartItemsUseCase(listOf(cartItem()))

        // Then
        assertThat(result).isEqualTo(CartWriteResult.Failed)
    }
}
