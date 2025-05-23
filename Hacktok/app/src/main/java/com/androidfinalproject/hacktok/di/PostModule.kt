package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.impl.PostRepositoryImpl
import com.androidfinalproject.hacktok.service.LikeService
import com.androidfinalproject.hacktok.service.impl.LikeServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PostModule {
    @Binds
    @Singleton
    abstract fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository
} 