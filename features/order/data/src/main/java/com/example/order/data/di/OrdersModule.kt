package com.example.order.data.di

import com.example.domain.repository.OrdersRepository
import com.example.order.data.repository.OrdersRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OrdersModule {

    @Binds
    abstract fun bindOrdersRepository(impl: OrdersRepositoryImpl): OrdersRepository
}
