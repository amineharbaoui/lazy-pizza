package com.example.di

import com.example.domain.repository.PhoneAuthRepository
import com.example.domain.usecase.SignInWithSmsCodeUseCase
import com.example.domain.usecase.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {
    @Provides
    fun provideSignInWithSmsCodeUseCase(repository: PhoneAuthRepository): SignInWithSmsCodeUseCase = SignInWithSmsCodeUseCase(repository)

    @Provides
    fun provideSignOutUseCase(repository: PhoneAuthRepository): SignOutUseCase = SignOutUseCase(repository)
}
