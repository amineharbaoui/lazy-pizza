package com.example.lazypizza.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val FIRESTORE_DB_NAME = "lazypizzadb"

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance(FIRESTORE_DB_NAME)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // Unscoped: each injection reads the system time zone of the moment, so a zone change reaches the next screen.
    @Provides
    fun provideClock(): Clock = Clock.systemDefaultZone()
}
