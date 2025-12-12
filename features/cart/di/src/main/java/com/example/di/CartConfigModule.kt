package com.example.di

import com.example.cart.di.BuildConfig
import com.example.data.datasource.util.CartTouchThrottle
import com.example.data.datasource.util.CartTtl
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
