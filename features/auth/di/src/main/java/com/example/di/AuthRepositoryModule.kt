package com.example.di

import com.example.data.repository.FirebasePhoneAuthRepository
import com.example.domain.repository.PhoneAuthRepository
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
    abstract fun bindPhoneAuthRepository(impl: FirebasePhoneAuthRepository): PhoneAuthRepository
}
