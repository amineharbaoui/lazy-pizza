package com.example.cart.data.di

import com.example.cart.data.BuildConfig
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
