package com.example.data.di

import com.example.data.repository.OrdersRepositoryImpl
import com.example.domain.repository.OrdersRepository
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
