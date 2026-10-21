package com.example.cart.domain.usecase

import app.cash.turbine.test
import io.mockk.bdd.given
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ObserveCartItemCountUseCaseTest {

    @MockK
    lateinit var observeCart: ObserveCartUseCase

    @InjectMockKs
    lateinit var observeCartItemCountUseCase: ObserveCartItemCountUseCase

    @Test
    suspend fun `invoke when cart has several lines then emits sum of quantities`() {
        // Given
        val cart = cartOf(cartItem(lineId = "a", quantity = 2), cartItem(lineId = "b", quantity = 3))
        given { observeCart() } returns flowOf(cart)

        // When
        observeCartItemCountUseCase().test {
            // Then
            assertThat(awaitItem()).isEqualTo(EXPECTED_COUNT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        const val EXPECTED_COUNT = 5
    }
}
