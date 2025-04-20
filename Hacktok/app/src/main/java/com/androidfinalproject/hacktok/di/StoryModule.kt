package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.StoryRepository
import com.androidfinalproject.hacktok.repository.impl.StoryRepositoryImpl
import com.androidfinalproject.hacktok.service.StoryService
import com.androidfinalproject.hacktok.service.impl.StoryServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoryModule {
    @Binds
    @Singleton
    abstract fun bindStoryService(storyServiceImpl: StoryServiceImpl): StoryService

    @Binds
    @Singleton
    abstract fun bindStoryRepository(storyRepositoryImpl: StoryRepositoryImpl): StoryRepository
}