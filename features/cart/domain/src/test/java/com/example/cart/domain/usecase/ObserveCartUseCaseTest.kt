package com.example.cart.domain.usecase

import app.cash.turbine.test
import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.repository.CartRepository
import io.mockk.bdd.given
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ObserveCartUseCaseTest {

    @MockK
    lateinit var repository: CartRepository

    @MockK
    lateinit var sessionRepository: SessionRepository

    @InjectMockKs
    lateinit var observeCartUseCase: ObserveCartUseCase

    private val guestCart = cartOf(cartItem(lineId = "guest-line"))
    private val userCart = cartOf(cartItem(lineId = "user-line"))

    @Test
    suspend fun `invoke when user is signed out then emits guest cart`() {
        // Given
        given { sessionRepository.userIdFlow } returns flowOf(null)
        given { repository.observeCart(CartOwner.Guest) } returns flowOf(guestCart)

        // When
        observeCartUseCase().test {
            // Then
            assertThat(awaitItem()).isEqualTo(guestCart)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    suspend fun `invoke when user is signed in then emits user cart`() {
        // Given
        given { sessionRepository.userIdFlow } returns flowOf(USER_ID)
        given { repository.observeCart(USER_OWNER) } returns flowOf(userCart)

        // When
        observeCartUseCase().test {
            // Then
            assertThat(awaitItem()).isEqualTo(userCart)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    suspend fun `invoke when user signs in then switches to user cart`() {
        // Given
        val userIdFlow = MutableStateFlow<String?>(null)
        given { sessionRepository.userIdFlow } returns userIdFlow
        given { repository.observeCart(CartOwner.Guest) } returns flowOf(guestCart)
        given { repository.observeCart(USER_OWNER) } returns flowOf(userCart)

        observeCartUseCase().test {
            skipItems(1)

            // When
            userIdFlow.value = USER_ID

            // Then
            assertThat(awaitItem()).isEqualTo(userCart)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    suspend fun `invoke when cart repository emits empty cart then emits empty cart`() {
        // Given
        given { sessionRepository.userIdFlow } returns flowOf(null)
        given { repository.observeCart(CartOwner.Guest) } returns flowOf(Cart())

        // When
        observeCartUseCase().test {
            // Then
            assertThat(awaitItem().items).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
