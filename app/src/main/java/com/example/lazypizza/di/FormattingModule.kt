package com.example.lazypizza.di

import com.example.menu.utils.formatting.CurrencyFormatter
import com.example.menu.utils.formatting.DefaultCurrencyFormatter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FormattingModule {

    @Binds
    @Singleton
    abstract fun bindCurrencyFormatter(impl: DefaultCurrencyFormatter): CurrencyFormatter
}
