package com.example.cart.domain.usecase

import app.cash.turbine.test
import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ObserveCartItemCountUseCaseTest {

    @Test
    fun `invoke when cart has several lines then emits sum of quantities`() = runTest {
        // Given
        val cart = cartOf(
            cartItem(lineId = FIRST_LINE_ID, quantity = FIRST_LINE_QUANTITY),
            cartItem(lineId = SECOND_LINE_ID, quantity = SECOND_LINE_QUANTITY),
        )
        val observeCart = ObserveCartUseCase(InMemoryCartRepository(cart), SignedOutSessionRepository())
        val observeCartItemCountUseCase = ObserveCartItemCountUseCase(observeCart)

        // When
        observeCartItemCountUseCase().test {
            // Then
            assertThat(awaitItem()).isEqualTo(EXPECTED_COUNT)
            awaitComplete()
        }
    }

    private class InMemoryCartRepository(private val cart: Cart) : CartRepository {
        override fun observeCart(owner: CartOwner): Flow<Cart> = flowOf(cart)

        override suspend fun addItem(
            owner: CartOwner,
            item: CartItem,
        ) = Unit

        override suspend fun replaceItem(
            owner: CartOwner,
            item: CartItem,
        ) = Unit

        override suspend fun updateItemQuantity(
            owner: CartOwner,
            lineId: String,
            quantity: Int,
        ) = Unit

        override suspend fun removeItem(
            owner: CartOwner,
            lineId: String,
        ) = Unit

        override suspend fun clear(owner: CartOwner) = Unit

        override suspend fun transferGuestCart(to: CartOwner.User) = Unit
    }

    private class SignedOutSessionRepository : SessionRepository {
        override val userIdFlow: Flow<String?> = flowOf(null)
        override val isSignedIn: Flow<Boolean> = flowOf(false)

        override suspend fun currentUserUid(): String? = null
    }

    private companion object {
        const val FIRST_LINE_ID = "line-1"
        const val SECOND_LINE_ID = "line-2"
        const val FIRST_LINE_QUANTITY = 2
        const val SECOND_LINE_QUANTITY = 3
        const val EXPECTED_COUNT = 5
    }
}
