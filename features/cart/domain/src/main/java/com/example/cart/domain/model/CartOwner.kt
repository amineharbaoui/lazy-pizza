package com.example.cart.domain.model

sealed interface CartOwner {
    data object Guest : CartOwner
    data class User(val userId: String) : CartOwner
}
