package com.example.auth.data.repository

import app.cash.turbine.test
import com.example.auth.data.AuthTestData
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SessionRepositoryImplTest {

    private val testData = AuthTestData()

    @Test
    fun `userIdFlow when the user signs in then signs out emits their uid then null`() = runTest {
        // Given
        testData.givenSignedOut()

        testData.sessionRepository.userIdFlow.test {
            assertThat(awaitItem()).isNull()

            // When
            testData.givenSignedIn(USER_ID)
            testData.givenSignedOut()

            // Then
            assertThat(awaitItem()).isEqualTo(USER_ID)
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `isSignedIn when the user signs in then emits true`() = runTest {
        // Given
        testData.givenSignedOut()

        testData.sessionRepository.isSignedIn.test {
            assertThat(awaitItem()).isFalse()

            // When
            testData.givenSignedIn(USER_ID)

            // Then
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun `currentUserUid when a user is signed in then returns their uid`() = runTest {
        // Given
        testData.givenSignedIn(USER_ID)

        // When
        val result = testData.sessionRepository.currentUserUid()

        // Then
        assertThat(result).isEqualTo(USER_ID)
    }

    @Test
    fun `currentUserUid when no user is signed in then returns null`() = runTest {
        // Given
        testData.givenSignedOut()

        // When
        val result = testData.sessionRepository.currentUserUid()

        // Then
        assertThat(result).isNull()
    }

    private companion object {
        const val USER_ID = "user-id-123"
    }
}
