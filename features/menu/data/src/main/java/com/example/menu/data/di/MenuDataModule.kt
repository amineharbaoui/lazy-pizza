package com.example.menu.data.di

import com.example.domain.repository.MenuRepository
import com.example.domain.repository.RecommendedItemsRepository
import com.example.menu.data.repository.MenuRepositoryImpl
import com.example.menu.data.repository.RecommendedItemsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MenuDataModule {

    @Binds
    @Singleton
    abstract fun bindMenuRepository(impl: MenuRepositoryImpl): MenuRepository

    @Binds
    @Singleton
    abstract fun bindRecommendedItemsRepository(impl: RecommendedItemsRepositoryImpl): RecommendedItemsRepository
}
