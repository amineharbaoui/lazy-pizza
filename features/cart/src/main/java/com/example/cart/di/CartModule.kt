package com.example.cart.di

import com.example.cart.data.repository.InMemoryCartRepository
import com.example.cart.data.repository.RecommendedItemsRepositoryImpl
import com.example.cart.domain.repository.CartRepository
import com.example.cart.domain.repository.RecommendedItemsRepository
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
    abstract fun bindCartRepository(impl: InMemoryCartRepository): CartRepository

    @Binds
    @Singleton
    abstract fun bindRecommendedItemsRepository(impl: RecommendedItemsRepositoryImpl): RecommendedItemsRepository
}