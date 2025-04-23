package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.service.ApiService
import com.androidfinalproject.hacktok.service.impl.ApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiModule {
    
    @Binds
    @Singleton
    abstract fun bindApiApiService(apiServiceImpl: ApiServiceImpl): ApiService
}