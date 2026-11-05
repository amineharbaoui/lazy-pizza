package com.example.auth.data.di

import com.example.auth.data.datasource.PhoneAuthDataSource
import com.example.auth.data.datasource.PhoneAuthDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataSourceModule {

    @Binds
    abstract fun bindPhoneAuthDataSource(impl: PhoneAuthDataSourceImpl): PhoneAuthDataSource
}
