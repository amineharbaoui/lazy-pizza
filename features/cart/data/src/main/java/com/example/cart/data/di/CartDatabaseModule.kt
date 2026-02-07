package com.example.cart.data.di

import android.content.Context
import androidx.room.Room
import com.example.cart.data.datasource.db.CartDatabase
import com.example.cart.data.datasource.db.dao.CartDao
import com.example.cart.data.datasource.db.dao.CartMetadataDao
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
    ).fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun provideCartDao(db: CartDatabase): CartDao = db.cartDao()

    @Provides
    fun provideCartMetadataDao(db: CartDatabase): CartMetadataDao = db.cartMetadataDao()
}
