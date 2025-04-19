package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.PostShareRepository
import com.androidfinalproject.hacktok.repository.impl.PostShareRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PostShareModule {

    @Binds
    @Singleton
    abstract fun bindPostShareRepository(
        impl: PostShareRepositoryImpl
    ): PostShareRepository
}