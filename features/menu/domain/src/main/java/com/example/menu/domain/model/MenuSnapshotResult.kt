package com.example.menu.domain.model

sealed interface MenuSnapshotResult {
    data class Success(val snapshot: MenuSnapshot) : MenuSnapshotResult

    /** Retryable: network, timeout or backend error. */
    data object Unavailable : MenuSnapshotResult
}
