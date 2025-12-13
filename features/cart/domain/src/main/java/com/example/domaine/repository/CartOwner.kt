package com.example.domaine.repository

sealed interface CartOwner {
    data object Guest : CartOwner
    data class User(val uid: String) : CartOwner

    val key: String
        get() = when (this) {
            Guest -> "guest"
            is User -> "user:$uid"
        }
}
