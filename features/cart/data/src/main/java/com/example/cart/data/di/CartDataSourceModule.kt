package com.example.cart.data.di

import com.example.cart.data.datasource.CartLocalDataSource
import com.example.cart.data.datasource.CartLocalDataSourceImpl
import com.example.cart.data.datasource.CartTransferLocalDataSource
import com.example.cart.data.datasource.CartTransferLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CartDataSourceModule {

    @Binds
    abstract fun bindCartLocalDataSource(impl: CartLocalDataSourceImpl): CartLocalDataSource

    @Binds
    abstract fun bindCartTransferLocalDataSource(impl: CartTransferLocalDataSourceImpl): CartTransferLocalDataSource
}
