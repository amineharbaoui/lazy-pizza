package com.example.di

import com.example.domain.repository.MenuRepository
import com.example.domain.usecase.ObserveMenuUseCase
import com.example.domain.usecase.ObservePizzaByIdUseCase
import com.example.domain.usecase.ObservePizzaDetailUseCase
import com.example.domain.usecase.ObserveToppingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MenuDomainModule {

    @Provides
    fun provideObserveMenuUseCase(repository: MenuRepository): ObserveMenuUseCase = ObserveMenuUseCase(repository)

    @Provides
    fun provideObservePizzaDetailUseCase(repository: MenuRepository): ObservePizzaDetailUseCase = ObservePizzaDetailUseCase(repository)

    @Provides
    fun provideObservePizzaByIdUseCase(repository: MenuRepository): ObservePizzaByIdUseCase = ObservePizzaByIdUseCase(repository)

    @Provides
    fun provideObserveToppingsUseCase(repository: MenuRepository): ObserveToppingsUseCase = ObserveToppingsUseCase(repository)
}
