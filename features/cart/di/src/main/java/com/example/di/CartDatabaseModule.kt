package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.datasource.local.CartDao
import com.example.data.datasource.local.CartDatabase
import com.example.data.datasource.local.CartMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartDatabaseModule {

    @Provides
    @Singleton
    fun provideCartDatabase(@ApplicationContext context: Context): CartDatabase = Room.databaseBuilder(
        context = context,
        klass = CartDatabase::class.java,
        name = "cart.db",
    ).build()

    @Provides
    fun provideCartDao(db: CartDatabase): CartDao = db.cartDao()

    @Provides
    fun provideCartMetadataDao(db: CartDatabase): CartMetadataDao = db.cartMetadataDao()
}
