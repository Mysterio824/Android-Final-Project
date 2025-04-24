package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.StatisticsRepository
import com.androidfinalproject.hacktok.repository.impl.StatisticsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StatisticsModule {
    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(statisticsRepositoryImpl: StatisticsRepositoryImpl): StatisticsRepository
}