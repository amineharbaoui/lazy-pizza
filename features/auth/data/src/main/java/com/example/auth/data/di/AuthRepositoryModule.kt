package com.example.auth.data.di

import android.app.Application
import com.example.auth.data.datasource.PhoneAuthRemoteDataSource
import com.example.auth.data.datasource.PhoneAuthRemoteDataSourceImpl
import com.example.auth.data.datasource.ResumedActivityTracker
import com.example.auth.data.repository.PhoneAuthRepositoryImpl
import com.example.auth.data.repository.SessionRepositoryImpl
import com.example.auth.domain.repository.PhoneAuthRepository
import com.example.auth.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    @Singleton
    internal abstract fun bindPhoneAuthRepository(impl: PhoneAuthRepositoryImpl): PhoneAuthRepository

    @Binds
    @Singleton
    internal abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    internal abstract fun bindPhoneAuthRemoteDataSource(impl: PhoneAuthRemoteDataSourceImpl): PhoneAuthRemoteDataSource

    @Binds
    @IntoSet
    internal abstract fun bindResumedActivityTracker(impl: ResumedActivityTracker): Application.ActivityLifecycleCallbacks
}
