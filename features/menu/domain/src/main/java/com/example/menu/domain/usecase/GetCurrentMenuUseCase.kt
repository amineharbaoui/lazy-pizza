package com.example.menu.domain.usecase

import com.example.menu.domain.model.CurrentMenuResult
import com.example.menu.domain.repository.MenuRepository
import javax.inject.Inject

class GetCurrentMenuUseCase @Inject constructor(private val repository: MenuRepository) {
    suspend operator fun invoke(): CurrentMenuResult = repository.getCurrentMenu()
}
