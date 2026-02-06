package com.example.auth.data.di

import com.example.auth.data.repository.PhoneAuthRepositoryImpl
import com.example.auth.data.repository.SessionRepositoryImpl
import com.example.auth.domain.repository.PhoneAuthRepository
import com.example.auth.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPhoneAuthRepository(impl: PhoneAuthRepositoryImpl): PhoneAuthRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
}
