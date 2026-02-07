package com.example.core.network.di

import com.example.core.common.NetworkMonitor
import com.example.core.network.AndroidNetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(impl: AndroidNetworkMonitor): NetworkMonitor
}
