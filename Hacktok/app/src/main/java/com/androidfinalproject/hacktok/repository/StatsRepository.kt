package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Stats
import com.androidfinalproject.hacktok.model.Timeframe
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface StatisticsRepository {
    fun observeUserStatistics(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date
    ): Flow<Result<Stats>>

    fun observePostStatistics(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date,
        includeBanned: Boolean = true
    ): Flow<Result<Stats>>

    fun observeCommentStatistics(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date,
        includeBanned: Boolean = true
    ): Flow<Result<Stats>>

    fun observeAllStatistics(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date
    ): Flow<Result<Stats>>
}