package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.impl.ChatRepositoryImpl
import com.androidfinalproject.hacktok.repository.ChatRepository
//import com.androidfinalproject.hacktok.service.ChatService
//import com.androidfinalproject.hacktok.service.impl.ChatServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository
    
//    @Binds
//    @Singleton
//    abstract fun bindChatService(ChatServiceImpl: ChatServiceImpl): ChatService
} 