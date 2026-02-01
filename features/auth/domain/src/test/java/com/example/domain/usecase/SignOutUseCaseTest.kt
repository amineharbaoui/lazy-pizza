package com.example.domain.usecase

import com.example.domain.repository.PhoneAuthRepository
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
    suspend fun whenCalled_thenCallsRepositorySignOut() {
        // Given
        coGiven { phoneAuthRepository.signOut() } just Runs

        // When
        signOutUseCase()

        // Then
        coThen(exactly = 1) { phoneAuthRepository.signOut() }
    }
}
