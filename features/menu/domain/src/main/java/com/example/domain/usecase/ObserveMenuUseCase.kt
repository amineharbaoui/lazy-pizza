package com.example.domain.usecase

import com.example.domain.model.MenuSection
import com.example.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMenuUseCase @Inject constructor(private val repository: MenuRepository) {
    operator fun invoke(): Flow<List<MenuSection>> = repository.observeMenuSections()
}
