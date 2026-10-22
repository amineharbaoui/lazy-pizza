package com.example.cart.data.mapper

import com.example.cart.domain.model.CartOwner
import javax.inject.Inject

class CartOwnerToKeyMapper @Inject constructor() {

    fun map(owner: CartOwner): String = when (owner) {
        CartOwner.Guest -> KEY_GUEST
        is CartOwner.User -> "$PREFIX_USER${owner.userId}"
    }

    private companion object {
        const val KEY_GUEST = "guest"
        const val PREFIX_USER = "user:"
    }
}
