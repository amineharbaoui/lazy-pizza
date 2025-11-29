package com.example.di

import com.example.data.repository.FirebasePhoneAuthRepository
import com.example.domain.repository.PhoneAuthRepository
import com.example.domain.usecase.SignInWithSmsCodeUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPhoneAuthRepository(impl: FirebasePhoneAuthRepository): PhoneAuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideSignInWithSmsCodeUseCase(
            repository: PhoneAuthRepository,
        ): SignInWithSmsCodeUseCase = SignInWithSmsCodeUseCase(repository)
    }
}
