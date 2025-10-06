package com.example.lazypizza.features.home.data.di

import com.example.lazypizza.features.home.data.repository.HomeRepositoryImpl
import com.example.lazypizza.features.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        repositoryImpl: HomeRepositoryImpl
    ): HomeRepository
}
