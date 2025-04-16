package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.impl.NotificationRepositoryImpl
import com.androidfinalproject.hacktok.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(NotificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
} 