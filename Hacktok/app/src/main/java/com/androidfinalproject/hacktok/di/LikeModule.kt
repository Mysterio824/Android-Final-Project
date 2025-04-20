package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.service.LikeService
import com.androidfinalproject.hacktok.service.impl.LikeServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LikeModule{
    @Binds
    @Singleton
    abstract fun bindLikeService(likeServiceImpl: LikeServiceImpl): LikeService
}
