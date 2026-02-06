package com.example.domain.usecase

import app.cash.turbine.test
import com.example.auth.domain.repository.SessionRepository
import com.example.auth.domain.usecase.ObserveIsSignedInUseCase
import io.mockk.bdd.given
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.MutableStateFlow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ObserveIsSignedInUseCaseTest {

    @MockK
    private lateinit var sessionRepository: SessionRepository

    @InjectMockKs
    private lateinit var useCase: ObserveIsSignedInUseCase

    @Test
    suspend fun `invoke when is signed in emits values then emits same values`() {
        // Given
        val isSignedInFlow = MutableStateFlow(false)
        given { sessionRepository.isSignedIn } returns isSignedInFlow

        // When
        useCase().test {
            // Then
            isSignedInFlow.tryEmit(false)
            assertThat(awaitItem()).isFalse()

            isSignedInFlow.tryEmit(true)
            assertThat(awaitItem()).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
