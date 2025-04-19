package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.ReportRepository
import com.androidfinalproject.hacktok.repository.impl.ReportRepositoryImpl
import com.androidfinalproject.hacktok.service.ReportService
import com.androidfinalproject.hacktok.service.impl.ReportServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportModule {
    @Binds
    @Singleton
    abstract fun bindReportRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository

    @Binds
    @Singleton
    abstract fun bindReportService(reportServiceImpl: ReportServiceImpl): ReportService
} 