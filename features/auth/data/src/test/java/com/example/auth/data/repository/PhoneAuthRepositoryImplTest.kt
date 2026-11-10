package com.example.auth.data.repository

import com.example.auth.data.datasource.PhoneAuthRemoteDataSource
import com.example.auth.data.model.RemoteUser
import com.example.auth.domain.model.AuthUser
import io.mockk.Runs
import io.mockk.bdd.coGiven
import io.mockk.bdd.coThen
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PhoneAuthRepositoryImplTest {

    @MockK
    private lateinit var phoneAuthRemoteDataSource: PhoneAuthRemoteDataSource

    @InjectMockKs
    private lateinit var phoneAuthRepository: PhoneAuthRepositoryImpl

    @Test
    suspend fun `signInWithCode when sign in succeeds then returns auth user`() {
        // Given
        val mockRemoteUser = RemoteUser(uid = "uid123", phoneNumber = "+123456789")
        val expected = AuthUser(uid = "uid123", phoneNumber = "+123456789")

        coGiven { phoneAuthRemoteDataSource.signInWithCode("", "") } returns mockRemoteUser

        // When
        val result = phoneAuthRepository.signInWithCode("", "")

        // Then
        assertThat(result.isSuccess).isTrue
        assertThat(result.getOrNull()).isEqualTo(expected)
        coThen { phoneAuthRemoteDataSource.signInWithCode("", "") }
    }

    @Test
    suspend fun `signInWithCode when exception thrown then returns failure`() {
        // Given
        val exception = IllegalStateException("Invalid code")

        coGiven { phoneAuthRemoteDataSource.signInWithCode("", "") } throws exception

        // When
        val result = phoneAuthRepository.signInWithCode("", "")

        // Then
        assertThat(result.isFailure).isTrue
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        coThen { phoneAuthRemoteDataSource.signInWithCode("", "") }
    }

    @Test
    suspend fun `signOut when called then invokes data source sign out`() {
        // Given
        coGiven { phoneAuthRemoteDataSource.signOut() } just Runs

        // When
        phoneAuthRepository.signOut()

        // Then
        coThen { phoneAuthRemoteDataSource.signOut() }
    }
}
