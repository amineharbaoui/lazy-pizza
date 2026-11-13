package com.example.auth.domain.usecase

import com.example.auth.domain.model.VerificationCodeStatus
import com.example.auth.domain.repository.PhoneAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SendVerificationCodeUseCase @Inject constructor(
    private val repository: PhoneAuthRepository,
) {
    operator fun invoke(phoneNumber: String): Flow<VerificationCodeStatus> = repository.sendVerificationCode(phoneNumber, RESEND_TIMEOUT)

    companion object {
        val RESEND_TIMEOUT: Duration = 60.seconds
    }
}
