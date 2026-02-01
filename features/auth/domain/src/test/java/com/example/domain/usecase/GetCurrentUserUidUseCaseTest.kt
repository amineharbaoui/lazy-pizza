package com.example.domain.usecase

import com.example.domain.repository.SessionRepository
import io.mockk.bdd.coGiven
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetCurrentUserUidUseCaseTest {

    @MockK
    lateinit var sessionRepository: SessionRepository

    @InjectMockKs
    lateinit var getCurrentUserUidUseCase: GetCurrentUserUidUseCase

    @Test
    suspend fun `invoke when current user uid is available then returns uid`() {
        // Given
        val expectedUid = "test_user_uid"
        coGiven { sessionRepository.currentUserUid() } returns expectedUid

        // When
        val result = getCurrentUserUidUseCase()

        // Then
        assertThat(result).isEqualTo(expectedUid)
    }

    @Test
    suspend fun `invoke when current user uid is null then returns null`() {
        // Given
        coGiven { sessionRepository.currentUserUid() } returns null

        // When
        val result = getCurrentUserUidUseCase()

        // Then
        assertThat(result).isNull()
    }
}
