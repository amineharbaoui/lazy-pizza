package com.example.di

import com.example.data.repository.CartRepositoryImpl
import com.example.data.repository.RecommendedItemsRepositoryImpl
import com.example.domain.repository.CartRepository
import com.example.domain.repository.RecommendedItemsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartDataModule {

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindRecommendedItemsRepository(impl: RecommendedItemsRepositoryImpl): RecommendedItemsRepository
}
