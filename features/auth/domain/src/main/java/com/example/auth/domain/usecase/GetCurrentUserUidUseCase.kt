package com.example.auth.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import javax.inject.Inject

class GetCurrentUserUidUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke() = sessionRepository.currentUserUid()
}
