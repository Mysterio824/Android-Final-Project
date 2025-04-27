package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.SavedPostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSavedPostRepository(): SavedPostRepository {
        return SavedPostRepository()
    }
} 