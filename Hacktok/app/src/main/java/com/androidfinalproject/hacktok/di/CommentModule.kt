package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.CommentRepository
import com.androidfinalproject.hacktok.repository.impl.CommentRepositoryImpl
import com.androidfinalproject.hacktok.service.CommentService
import com.androidfinalproject.hacktok.service.impl.CommentServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommentModule {

    @Binds
    @Singleton
    abstract fun bindCommentRepository(commentRepositoryImpl: CommentRepositoryImpl): CommentRepository

    @Binds
    @Singleton
    abstract fun bindCommentService(commentServiceImpl: CommentServiceImpl): CommentService
} 