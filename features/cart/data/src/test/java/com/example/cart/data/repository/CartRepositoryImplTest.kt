package com.example.cart.data.repository

import app.cash.turbine.test
import com.example.cart.data.CartTestData
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.core.model.ProductCategory
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class CartRepositoryImplTest {

    private val testData = CartTestData(Clock.fixed(NOW, ZoneOffset.UTC))
    private val repository = testData.cartRepository

    @Test
    fun `observeCart when the cart changed within its lifetime then emits its lines`() = runTest {
        // Given
        val cola = cola()
        testData.givenCart(USER, cola, updatedAt = NOW.minus(CartTestData.CART_TTL))

        // When
        repository.observeCart(USER).test {
            // Then
            assertThat(awaitItem().items).containsExactly(cola)
        }
    }

    @Test
    fun `observeCart when the cart outlived its lifetime then emits an empty cart`() = runTest {
        // Given
        testData.givenCart(USER, cola(), updatedAt = NOW.minus(CartTestData.CART_TTL).minusSeconds(1))

        // When
        repository.observeCart(USER).test {
            // Then
            assertThat(awaitItem().items).isEmpty()
        }
    }

    @Test
    fun `addItem when the cart outlived its lifetime then keeps only the new line`() = runTest {
        // Given
        testData.givenCart(USER, cola(), updatedAt = NOW.minus(CartTestData.CART_TTL).minusSeconds(1))
        val water = cola(lineId = WATER_LINE_ID, name = WATER)

        // When
        repository.addItem(USER, water)

        // Then
        assertThat(testData.cartLines(USER)).containsExactly(water)
    }

    @Test
    fun `updateItemQuantity when the quantity is zero then removes the line`() = runTest {
        // Given
        testData.givenCart(USER, cola())

        // When
        repository.updateItemQuantity(USER, lineId = COLA_LINE_ID, quantity = 0)

        // Then
        assertThat(testData.cartLines(USER)).isEmpty()
    }

    @Test
    fun `transferGuestCart when the guest has lines then moves them to the user`() = runTest {
        // Given
        val cola = cola()
        testData.givenCart(CartOwner.Guest, cola)

        // When
        repository.transferGuestCart(to = USER)

        // Then
        assertThat(testData.cartLines(USER)).containsExactly(cola)
    }

    @Test
    fun `clear when owner is user then empties only the user's cart`() = runTest {
        // Given
        val guestCola = cola()
        testData.givenCart(USER, cola())
        testData.givenCart(CartOwner.Guest, guestCola)

        // When
        repository.clear(owner = USER)

        // Then
        assertThat(testData.cartLines(USER)).isEmpty()
        assertThat(testData.cartLines(CartOwner.Guest)).containsExactly(guestCola)
    }

    private fun cola(
        lineId: String = COLA_LINE_ID,
        name: String = COLA,
    ) = CartItem.Other(
        lineId = lineId,
        productId = lineId,
        name = name,
        imageUrl = IMAGE_URL,
        quantity = 1,
        category = ProductCategory.DRINK,
        unitPrice = PRICE,
    )

    private companion object {
        val USER = CartOwner.User("user-123")
        const val COLA_LINE_ID = "line-cola"
        const val COLA = "Cola"
        const val WATER_LINE_ID = "line-water"
        const val WATER = "Water"
        const val IMAGE_URL = "https://example.com/image.png"
        const val PRICE = 1.99
        val NOW: Instant = Instant.parse("2026-11-09T12:00:00Z")
    }
}
