package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.SecretCrushRepository
import com.androidfinalproject.hacktok.repository.impl.SecretCrushRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecretCrushModule {
    @Binds
    @Singleton
    abstract fun bindSecretCrushRepository(secretCrushRepositoryImpl: SecretCrushRepositoryImpl): SecretCrushRepository
}