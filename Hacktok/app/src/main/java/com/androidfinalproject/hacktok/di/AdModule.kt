package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.service.AdService
import com.androidfinalproject.hacktok.service.impl.AdServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdModule {
    @Binds
    @Singleton
    abstract fun bindAdService(adServiceImpl: AdServiceImpl): AdService
} 