package com.example.di

import com.example.domain.repository.CartRepository
import com.example.domain.repository.RecommendedItemsRepository
import com.example.domain.usecase.AddCartItemUseCase
import com.example.domain.usecase.ClearCartUseCase
import com.example.domain.usecase.ObserveCartItemCountUseCase
import com.example.domain.usecase.ObserveCartUseCase
import com.example.domain.usecase.ObserveRecommendedItemsUseCase
import com.example.domain.usecase.RemoveCartItemUseCase
import com.example.domain.usecase.UpdateCartItemQuantityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CartDomainModule {

    @Provides
    fun provideAddCartItemUseCase(repository: CartRepository) = AddCartItemUseCase(repository)

    @Provides
    fun provideClearCartUseCase(repository: CartRepository) = ClearCartUseCase(repository)

    @Provides
    fun provideObserveCartUseCase(repository: CartRepository) = ObserveCartUseCase(repository)

    @Provides
    fun provideObserveRecommendedItemsUseCase(repository: RecommendedItemsRepository) = ObserveRecommendedItemsUseCase(repository)

    @Provides
    fun provideRemoveCartItemUseCase(repository: CartRepository) = RemoveCartItemUseCase(repository)

    @Provides
    fun provideUpdateCartItemQuantityUseCase(repository: CartRepository) = UpdateCartItemQuantityUseCase(repository)

    @Provides
    fun provideObserveCartItemCountUseCase(repository: CartRepository) = ObserveCartItemCountUseCase(repository)
}
