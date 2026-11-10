package com.example.cart.data.di

import com.example.cart.data.datasource.CartLocalDataSource
import com.example.cart.data.datasource.CartLocalDataSourceImpl
import com.example.cart.data.repository.CartRepositoryImpl
import com.example.cart.data.repository.UnavailableItemsRepositoryImpl
import com.example.cart.domain.repository.CartRepository
import com.example.cart.domain.repository.UnavailableItemsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartRepositoryModule {

    @Binds
    @Singleton
    internal abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    // One store for the whole app: a reorder writes it, the Cart screen reads it.
    @Binds
    @Singleton
    internal abstract fun bindUnavailableItemsRepository(impl: UnavailableItemsRepositoryImpl): UnavailableItemsRepository

    @Binds
    internal abstract fun bindCartLocalDataSource(impl: CartLocalDataSourceImpl): CartLocalDataSource
}
