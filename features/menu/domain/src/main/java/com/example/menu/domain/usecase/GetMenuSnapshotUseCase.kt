package com.example.menu.domain.usecase

import com.example.menu.domain.model.MenuSnapshotResult
import com.example.menu.domain.repository.MenuRepository
import javax.inject.Inject

class GetMenuSnapshotUseCase @Inject constructor(private val repository: MenuRepository) {
    suspend operator fun invoke(): MenuSnapshotResult = repository.getMenuSnapshot()
}
