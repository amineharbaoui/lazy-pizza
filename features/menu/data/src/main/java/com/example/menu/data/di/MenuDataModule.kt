package com.example.menu.data.di

import com.example.menu.data.datasource.ProductRemoteDataSource
import com.example.menu.data.datasource.ProductRemoteDataSourceImpl
import com.example.menu.data.repository.MenuRepositoryImpl
import com.example.menu.data.repository.RecommendedItemsRepositoryImpl
import com.example.menu.domain.repository.MenuRepository
import com.example.menu.domain.repository.RecommendedItemsRepository
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
    internal abstract fun bindMenuRepository(impl: MenuRepositoryImpl): MenuRepository

    @Binds
    @Singleton
    internal abstract fun bindRecommendedItemsRepository(impl: RecommendedItemsRepositoryImpl): RecommendedItemsRepository

    @Binds
    internal abstract fun bindProductRemoteDataSource(impl: ProductRemoteDataSourceImpl): ProductRemoteDataSource
}
