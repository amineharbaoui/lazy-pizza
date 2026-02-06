package com.example.domain.usecase

import com.example.auth.domain.repository.PhoneAuthRepository
import com.example.auth.domain.usecase.SignOutUseCase
import io.mockk.Runs
import io.mockk.bdd.coGiven
import io.mockk.bdd.coThen
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SignOutUseCaseTest {
    @MockK
    private lateinit var phoneAuthRepository: PhoneAuthRepository

    @InjectMockKs
    private lateinit var signOutUseCase: SignOutUseCase

    @Test
    suspend fun `invoke when called then calls repository sign out`() {
        // Given
        coGiven { phoneAuthRepository.signOut() } just Runs

        // When
        signOutUseCase()

        // Then
        coThen(exactly = 1) { phoneAuthRepository.signOut() }
    }
}
