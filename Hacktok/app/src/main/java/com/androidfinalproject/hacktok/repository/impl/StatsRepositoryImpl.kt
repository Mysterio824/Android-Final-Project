package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.StatPoint
import com.androidfinalproject.hacktok.model.Stats
import com.androidfinalproject.hacktok.model.Timeframe
import com.androidfinalproject.hacktok.repository.StatisticsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StatisticsRepository {
    private val TAG = "StatisticsRepository"
    private val usersCollection = firestore.collection("users")
    private val postsCollection = firestore.collection("posts")
    private val commentsCollection = firestore.collection("comments")

    override fun observeUserStatistics(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date
    ): Flow<Result<Stats>> = callbackFlow {
        val dateFormat = getDateFormat(timeframe)

        try {
            val userListener = usersCollection.get()
                .addOnSuccessListener { snapshot ->
                    val totalUsers = snapshot.size()

                    // Calculate statistics based on user creation date
                    val userStatPoints = calculateStatPoints(
                        documents = snapshot.documents,
                        timeframe = timeframe,
                        startDate = startDate,
                        endDate = endDate,
                        dateFormat = dateFormat,
                        createdAtField = "createdAt"
                    )

                    // Calculate new users in period based on timeframe
                    val newUsersInPeriod = when (timeframe) {
                        Timeframe.DAY -> {
                            val oneDayAgo = getDateBefore(endDate, Calendar.DAY_OF_MONTH, 1)
                            countUsersInRange(snapshot.documents, oneDayAgo, endDate)
                        }
                        Timeframe.MONTH -> {
                            val oneMonthAgo = getDateBefore(endDate, Calendar.MONTH, 1)
                            countUsersInRange(snapshot.documents, oneMonthAgo, endDate)
                        }
                        Timeframe.YEAR -> {
                            val oneYearAgo = getDateBefore(endDate, Calendar.YEAR, 1)
                            countUsersInRange(snapshot.documents, oneYearAgo, endDate)
                        }
                    }

                    // Calculate percentage change
                    val userPercentChange = if (totalUsers > 0) {
                        (newUsersInPeriod.toFloat() / totalUsers) * 100
                    } else {
                        0f
                    }

                    val statisticsData = Stats(
                        userStats = userStatPoints,
                        totalUsers = totalUsers,
                        newUsersInPeriod = newUsersInPeriod,
                        userPercentChange = userPercentChange
                    )

                    trySend(Result.success(statisticsData))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting user statistics", exception)
                    trySend(Result.failure(exception))
                }

            awaitClose {
                // Cleanup listeners if needed
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in observeUserStatistics", e)
            trySend(Result.failure(e))
            close(e)
        }
    }

    // Helper function to count users in a date range
    private fun countUsersInRange(
        users: List<com.google.firebase.firestore.DocumentSnapshot>,
        startDate: Date,
        endDate: Date
    ): Int {
        return users.count { doc ->
            val createdAt = doc.getTimestamp("createdAt")?.toDate()
            createdAt != null && createdAt >= startDate && createdAt <= endDate
        }
    }

    override fun observePostStatistics(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date,
        includeBanned: Boolean
    ): Flow<Result<Stats>> = callbackFlow {
        val dateFormat = getDateFormat(timeframe)

        try {
            val postListener = postsCollection.get()
                .addOnSuccessListener { snapshot ->
                    val allPosts = snapshot.documents
                    val totalPosts = allPosts.size

                    // Tách bài viết bị cấm và không bị cấm
                    val normalPosts = allPosts.filter { !it.getBoolean("isBanned").orFalse() }
                    val bannedPosts = allPosts.filter { it.getBoolean("isBanned").orFalse() }
                    val totalBannedPosts = bannedPosts.size

                    // Tính toán thống kê cho bài viết bình thường
                    val postStatPoints = calculateStatPoints(
                        documents = normalPosts,
                        timeframe = timeframe,
                        startDate = startDate,
                        endDate = endDate,
                        dateFormat = dateFormat,
                        createdAtField = "createdAt"
                    )

                    // Tính toán thống kê cho bài viết bị cấm
                    val bannedPostStatPoints = calculateStatPoints(
                        documents = bannedPosts,
                        timeframe = timeframe,
                        startDate = startDate,
                        endDate = endDate,
                        dateFormat = dateFormat,
                        createdAtField = "createdAt"
                    )

                    // Tính số lượng bài viết mới trong khoảng thời gian
                    val newPostsInPeriod = postStatPoints.sumOf { it.count }
                    val newBannedPostsInPeriod = bannedPostStatPoints.sumOf { it.count }

                    // Tính phần trăm thay đổi
                    val postPercentChange = if (totalPosts - totalBannedPosts > 0) {
                        (newPostsInPeriod.toFloat() / (totalPosts - totalBannedPosts)) * 100
                    } else {
                        0f
                    }

                    val bannedPostPercentChange = if (totalBannedPosts > 0) {
                        (newBannedPostsInPeriod.toFloat() / totalBannedPosts) * 100
                    } else {
                        0f
                    }

                    val statisticsData = Stats(
                        postStats = postStatPoints,
                        bannedPostStats = bannedPostStatPoints,
                        totalPosts = totalPosts - totalBannedPosts,
                        totalBannedPosts = totalBannedPosts,
                        newPostsInPeriod = newPostsInPeriod,
                        newBannedPostsInPeriod = newBannedPostsInPeriod,
                        postPercentChange = postPercentChange,
                        bannedPostPercentChange = bannedPostPercentChange
                    )

                    trySend(Result.success(statisticsData))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting post statistics", exception)
                    trySend(Result.failure(exception))
                }

            awaitClose {
                // Dọn dẹp listeners nếu cần
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in observePostStatistics", e)
            trySend(Result.failure(e))
            close(e)
        }
    }

    override fun observeCommentStatistics(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date,
        includeBanned: Boolean
    ): Flow<Result<Stats>> = callbackFlow {
        val dateFormat = getDateFormat(timeframe)

        try {
            val commentListener = commentsCollection.get()
                .addOnSuccessListener { snapshot ->
                    val allComments = snapshot.documents
                    val totalComments = allComments.size

                    // Tách bình luận bị cấm và không bị cấm
                    val normalComments = allComments.filter { !it.getBoolean("isBanned").orFalse() }
                    val bannedComments = allComments.filter { it.getBoolean("isBanned").orFalse() }
                    val totalBannedComments = bannedComments.size

                    // Tính toán thống kê cho bình luận bình thường
                    val commentStatPoints = calculateStatPoints(
                        documents = normalComments,
                        timeframe = timeframe,
                        startDate = startDate,
                        endDate = endDate,
                        dateFormat = dateFormat,
                        createdAtField = "createdAt"
                    )

                    // Tính toán thống kê cho bình luận bị cấm
                    val bannedCommentStatPoints = calculateStatPoints(
                        documents = bannedComments,
                        timeframe = timeframe,
                        startDate = startDate,
                        endDate = endDate,
                        dateFormat = dateFormat,
                        createdAtField = "createdAt"
                    )

                    // Tính số lượng bình luận mới trong khoảng thời gian
                    val newCommentsInPeriod = commentStatPoints.sumOf { it.count }
                    val newBannedCommentsInPeriod = bannedCommentStatPoints.sumOf { it.count }

                    // Tính phần trăm thay đổi
                    val commentPercentChange = if (totalComments - totalBannedComments > 0) {
                        (newCommentsInPeriod.toFloat() / (totalComments - totalBannedComments)) * 100
                    } else {
                        0f
                    }

                    val bannedCommentPercentChange = if (totalBannedComments > 0) {
                        (newBannedCommentsInPeriod.toFloat() / totalBannedComments) * 100
                    } else {
                        0f
                    }

                    // Tính toán thống kê theo thời gian (daily, monthly, yearly)
                    val now = Date()
                    val oneDayAgo = getDateBefore(now, Calendar.DAY_OF_MONTH, 1)
                    val oneMonthAgo = getDateBefore(now, Calendar.MONTH, 1)
                    val oneYearAgo = getDateBefore(now, Calendar.YEAR, 1)

                    val dailyCount = countCommentsInRange(normalComments, oneDayAgo, now)
                    val monthlyCount = countCommentsInRange(normalComments, oneMonthAgo, now)
                    val yearlyCount = countCommentsInRange(normalComments, oneYearAgo, now)

                    val statisticsData = Stats(
                        commentStats = commentStatPoints,
                        bannedCommentStats = bannedCommentStatPoints,
                        totalComments = totalComments - totalBannedComments,
                        totalBannedComments = totalBannedComments,
                        newCommentsInPeriod = newCommentsInPeriod,
                        newBannedCommentsInPeriod = newBannedCommentsInPeriod,
                        commentPercentChange = commentPercentChange,
                        bannedCommentPercentChange = bannedCommentPercentChange,
                        dailyCount = dailyCount,
                        monthlyCount = monthlyCount,
                        yearlyCount = yearlyCount
                    )

                    trySend(Result.success(statisticsData))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting comment statistics", exception)
                    trySend(Result.failure(exception))
                }

            awaitClose {
                // Dọn dẹp listeners nếu cần
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in observeCommentStatistics", e)
            trySend(Result.failure(e))
            close(e)
        }
    }

    override fun observeAllStatistics(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date
    ): Flow<Result<Stats>> {
        val userStatsFlow = observeUserStatistics(timeframe, startDate, endDate)
        val postStatsFlow = observePostStatistics(timeframe, startDate, endDate)
        val commentStatsFlow = observeCommentStatistics(timeframe, startDate, endDate)

        return combine(userStatsFlow, postStatsFlow, commentStatsFlow) { userResult, postResult, commentResult ->
            try {
                val userStats = userResult.getOrNull()
                val postStats = postResult.getOrNull()
                val commentStats = commentResult.getOrNull()

                if (userStats != null && postStats != null && commentStats != null) {
                    Result.success(Stats(
                        // User stats
                        userStats = userStats.userStats,
                        totalUsers = userStats.totalUsers,
                        newUsersInPeriod = userStats.newUsersInPeriod,
                        userPercentChange = userStats.userPercentChange,

                        // Post stats
                        postStats = postStats.postStats,
                        bannedPostStats = postStats.bannedPostStats,
                        totalPosts = postStats.totalPosts,
                        totalBannedPosts = postStats.totalBannedPosts,
                        newPostsInPeriod = postStats.newPostsInPeriod,
                        newBannedPostsInPeriod = postStats.newBannedPostsInPeriod,
                        postPercentChange = postStats.postPercentChange,
                        bannedPostPercentChange = postStats.bannedPostPercentChange,

                        // Comment stats
                        commentStats = commentStats.commentStats,
                        bannedCommentStats = commentStats.bannedCommentStats,
                        totalComments = commentStats.totalComments,
                        totalBannedComments = commentStats.totalBannedComments,
                        newCommentsInPeriod = commentStats.newCommentsInPeriod,
                        newBannedCommentsInPeriod = commentStats.newBannedCommentsInPeriod,
                        commentPercentChange = commentStats.commentPercentChange,
                        bannedCommentPercentChange = commentStats.bannedCommentPercentChange,

                        // Time-based stats
                        dailyCount = commentStats.dailyCount,
                        monthlyCount = commentStats.monthlyCount,
                        yearlyCount = commentStats.yearlyCount
                    ))
                } else {
                    val error = userResult.exceptionOrNull() ?:
                    postResult.exceptionOrNull() ?:
                    commentResult.exceptionOrNull() ?:
                    Exception("Unknown error")
                    Result.failure(error)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Hàm trợ giúp

    private fun getDateFormat(timeframe: Timeframe): SimpleDateFormat {
        return when (timeframe) {
            Timeframe.DAY -> SimpleDateFormat("MMM dd", Locale.getDefault())
            Timeframe.MONTH -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
            Timeframe.YEAR -> SimpleDateFormat("yyyy", Locale.getDefault())
        }
    }

    private fun calculateStatPoints(
        documents: List<com.google.firebase.firestore.DocumentSnapshot>,
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date,
        dateFormat: SimpleDateFormat,
        createdAtField: String
    ): List<StatPoint> {
        // Nhóm các mục theo khoảng thời gian (ngày, tháng, năm)
        val statPointsMap = mutableMapOf<String, MutableList<Date>>()

        // Khởi tạo các khoảng thời gian dựa trên timeframe
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate
        endCalendar.set(Calendar.HOUR_OF_DAY, 23)
        endCalendar.set(Calendar.MINUTE, 59)
        endCalendar.set(Calendar.SECOND, 59)
        endCalendar.set(Calendar.MILLISECOND, 999)

        while (calendar.time <= endCalendar.time) {
            val currentDate = calendar.time
            val label = dateFormat.format(currentDate)
            statPointsMap[label] = mutableListOf()

            when (timeframe) {
                Timeframe.DAY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
                Timeframe.MONTH -> {
                    calendar.add(Calendar.MONTH, 1)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                }
                Timeframe.YEAR -> {
                    calendar.add(Calendar.YEAR, 1)
                    calendar.set(Calendar.MONTH, 0)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                }
            }
        }

        // Nhóm các mục theo ngày tạo
        for (document in documents) {
            val createdAt = document.getTimestamp(createdAtField)?.toDate()
            if (createdAt != null) {
                val docCalendar = Calendar.getInstance()
                docCalendar.time = createdAt
                
                // Normalize the document date to match the timeframe
                when (timeframe) {
                    Timeframe.DAY -> {
                        docCalendar.set(Calendar.HOUR_OF_DAY, 0)
                        docCalendar.set(Calendar.MINUTE, 0)
                        docCalendar.set(Calendar.SECOND, 0)
                        docCalendar.set(Calendar.MILLISECOND, 0)
                    }
                    Timeframe.MONTH -> {
                        docCalendar.set(Calendar.DAY_OF_MONTH, 1)
                        docCalendar.set(Calendar.HOUR_OF_DAY, 0)
                        docCalendar.set(Calendar.MINUTE, 0)
                        docCalendar.set(Calendar.SECOND, 0)
                        docCalendar.set(Calendar.MILLISECOND, 0)
                    }
                    Timeframe.YEAR -> {
                        docCalendar.set(Calendar.MONTH, 0)
                        docCalendar.set(Calendar.DAY_OF_MONTH, 1)
                        docCalendar.set(Calendar.HOUR_OF_DAY, 0)
                        docCalendar.set(Calendar.MINUTE, 0)
                        docCalendar.set(Calendar.SECOND, 0)
                        docCalendar.set(Calendar.MILLISECOND, 0)
                    }
                }
                
                val normalizedDate = docCalendar.time
                if (normalizedDate >= startDate && normalizedDate <= endDate) {
                    val label = dateFormat.format(normalizedDate)
                    statPointsMap[label]?.add(normalizedDate)
                }
            }
        }

        // Convert to StatPoint list
        return statPointsMap.map { (label, dates) ->
            StatPoint(
                label = label,
                count = dates.size,
                date = dates.firstOrNull() ?: startDate
            )
        }.sortedBy { it.date }
    }

    private fun countCommentsInRange(
        comments: List<com.google.firebase.firestore.DocumentSnapshot>,
        startDate: Date,
        endDate: Date
    ): Int {
        return comments.count { doc ->
            val createdAt = doc.getTimestamp("createdAt")?.toDate()
            createdAt != null && createdAt >= startDate && createdAt <= endDate
        }
    }

    private fun getDateBefore(date: Date, calendarField: Int, amount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(calendarField, -amount)
        return calendar.time
    }

    // Extension function để xử lý Boolean? từ Firestore
    private fun Boolean?.orFalse(): Boolean = this ?: false
}