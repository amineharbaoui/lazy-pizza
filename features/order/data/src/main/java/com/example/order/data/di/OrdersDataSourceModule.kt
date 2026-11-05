package com.example.order.data.di

import com.example.order.data.datasource.OrdersRemoteDataSource
import com.example.order.data.datasource.OrdersRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OrdersDataSourceModule {

    @Binds
    abstract fun bindOrdersRemoteDataSource(impl: OrdersRemoteDataSourceImpl): OrdersRemoteDataSource
}
