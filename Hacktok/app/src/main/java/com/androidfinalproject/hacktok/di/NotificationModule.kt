package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.impl.NotificationRepositoryImpl
import com.androidfinalproject.hacktok.repository.NotificationRepository
import com.androidfinalproject.hacktok.service.NotificationService
import com.androidfinalproject.hacktok.service.impl.NotificationServiceImpl
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
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
    
    @Binds
    @Singleton
    abstract fun bindNotificationService(notificationServiceImpl: NotificationServiceImpl): NotificationService
} 