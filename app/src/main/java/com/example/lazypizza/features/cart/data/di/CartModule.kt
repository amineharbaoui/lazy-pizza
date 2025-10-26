package com.example.lazypizza.features.cart.data.di

import com.example.lazypizza.features.cart.data.repository.InMemoryCartRepository
import com.example.lazypizza.features.cart.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: InMemoryCartRepository
    ): CartRepository
}