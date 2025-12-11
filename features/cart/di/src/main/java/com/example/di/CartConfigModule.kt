package com.example.di

import com.example.data.datasource.local.CartTouchThrottle
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
    fun provideCartTtl(): Long = BuildConfig.CART_TTL_SECONDS

    @Provides
    @CartTouchThrottle
    fun provideCartTouchThrottleSeconds(): Long = BuildConfig.CART_TOUCH_THROTTLE_SECONDS
}
