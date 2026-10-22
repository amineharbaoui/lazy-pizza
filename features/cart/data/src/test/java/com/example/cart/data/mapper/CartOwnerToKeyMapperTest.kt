package com.example.cart.data.mapper

import com.example.cart.domain.model.CartOwner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CartOwnerToKeyMapperTest {

    private val mapper = CartOwnerToKeyMapper()

    @Test
    fun `map when owner is guest then returns guest key`() {
        // When
        val result = mapper.map(CartOwner.Guest)

        // Then
        assertThat(result).isEqualTo(GUEST_KEY)
    }

    @Test
    fun `map when owner is user then returns prefixed user key`() {
        // When
        val result = mapper.map(CartOwner.User(USER_ID))

        // Then
        assertThat(result).isEqualTo(USER_KEY)
    }

    private companion object {
        const val USER_ID = "user-123"

        // Persisted Room keys: changing them orphans existing carts.
        const val GUEST_KEY = "guest"
        const val USER_KEY = "user:user-123"
    }
}
