package com.example.auth.domain.usecase

import com.example.auth.domain.model.AuthUser
import com.example.auth.domain.repository.PhoneAuthRepository
import io.mockk.bdd.coGiven
import io.mockk.bdd.coThen
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SignInWithSmsCodeUseCaseTest {
    @MockK
    private lateinit var phoneAuthRepository: PhoneAuthRepository

    @InjectMockKs
    private lateinit var useCase: SignInWithSmsCodeUseCase

    @Test
    suspend fun `invoke when sign in succeeds then returns auth user`() {
        // Given
        val verificationId = "test-verification-id"
        val smsCode = "123456"
        val authUser = AuthUser(uid = "test-uid", phoneNumber = "+123456789")
        coGiven { phoneAuthRepository.signInWithCode(verificationId, smsCode) } returns Result.success(authUser)

        // When
        val result = useCase(verificationId, smsCode)

        // Then
        assertThat(result.isSuccess).isTrue
        assertThat(result.getOrNull()).isEqualTo(authUser)
        coThen { phoneAuthRepository.signInWithCode(verificationId, smsCode) }
    }

    @Test
    suspend fun `invoke when sign in fails then returns failure`() {
        // Given
        val verificationId = "test-verification-id"
        val smsCode = "123456"
        val exception = Exception("Sign-in failed")
        coGiven { phoneAuthRepository.signInWithCode(verificationId, smsCode) } returns Result.failure(exception)

        // When
        val result = useCase(verificationId, smsCode)

        // Then
        assertThat(result.isFailure).isTrue
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        coThen { phoneAuthRepository.signInWithCode(verificationId, smsCode) }
    }
}
