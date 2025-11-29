package com.example.di

import com.example.data.datasource.local.CartTtl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CartConfigModule {

    @Provides
    @CartTtl
    fun provideCartTtl(): Int = BuildConfig.CART_TTL_MINUTES
}
