package com.example.menu.data.di

import com.example.menu.data.datasource.ProductRemoteDataSource
import com.example.menu.data.datasource.ProductRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDataSourceModule {

    @Binds
    abstract fun bindProductRemoteDataSource(impl: ProductRemoteDataSourceImpl): ProductRemoteDataSource
}
