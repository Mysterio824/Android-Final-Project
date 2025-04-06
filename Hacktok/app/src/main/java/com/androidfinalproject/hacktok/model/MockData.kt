package com.androidfinalproject.hacktok.model

import com.androidfinalproject.hacktok.ui.userStatistic.Timeframe
import com.androidfinalproject.hacktok.ui.userStatistic.UserStatPoint
import com.androidfinalproject.hacktok.ui.userStatistic.UserStatisticsState
import java.util.Calendar
import java.util.Date

object MockData {
    val mockUsers = listOf(
        User("1", "john", "john@example.com", role = UserRole.USER),
        User("2", "jane_smith", "jane@example.com", role = UserRole.MODERATOR),
        User("3", "bob_jones", "bob@example.com", role = UserRole.ADMIN),
        User("4", "admin_super", "admin@example.com", role = UserRole.SUPER_ADMIN)
    )

    val mockPosts = listOf(
        Post("1", "Hello world!", "2", likeCount = 10),
        Post("2", "Learning Kotlin!", "2", likeCount = 1000),
        Post("3", "Jetpack Compose is fun!", "2", likeCount = 123)
    )

    val mockComments = listOf(
        Comment("1", "Hello world", "10", "1"),
        Comment("2", "Hello world", "10", "1"),
        Comment("3", "Hello world", "10", "1"),
        Comment("4", "Hello world", "10", "1"),
        Comment("5", "Hello world", "10", "1"),
        Comment("6", "Great post! I really enjoyed reading this.","5", "2"),
        Comment("7", "I have a question about this. Can you elaborate more?", "2", "2"),
        Comment("8", "This is exactly what I needed to know. Thanks for sharing!", "8", "2")
    )

    // Added mockUserRoles
    val mockUserRoles = listOf(
        UserRole.USER,
        UserRole.MODERATOR,
        UserRole.ADMIN,
        UserRole.SUPER_ADMIN
    )

    val mockReport = listOf(
        // Report on a post
        Report(
            id = "r1",
            reportedBy = "1", // john (USER)
            type = "post",
            targetId = "1", // "Hello world!" post
            reason = "Inappropriate content",
            createdAt = Date(),
            status = "pending"
        ),
        Report(
            id = "r2",
            reportedBy = "2", // jane_smith (MODERATOR)
            type = "post",
            targetId = "2", // "Learning Kotlin!" post
            reason = "Spam",
            createdAt = Date(),
            status = "resolved",
            resolvedBy = "3", // bob_jones (ADMIN)
            resolutionNote = "Post removed due to excessive self-promotion."
        ),
        // Report on a comment
        Report(
            id = "r3",
            reportedBy = "3", // bob_jones (ADMIN)
            type = "comment",
            targetId = "6", // "Great post! I really enjoyed reading this."
            reason = "Offensive language",
            createdAt = Date(),
            status = "pending"
        ),
        Report(
            id = "r4",
            reportedBy = "1", // john (USER)
            type = "comment",
            targetId = "7", // "I have a question about this. Can you elaborate more?"
            reason = "Harassment",
            createdAt = Date(),
            status = "resolved",
            resolvedBy = "4", // admin_super (SUPER_ADMIN)
            resolutionNote = "Comment hidden and user warned."
        ),
        // Report on a user
        Report(
            id = "r5",
            reportedBy = "2", // jane_smith (MODERATOR)
            type = "user",
            targetId = "1", // john (USER)
            reason = "Repeated rule violations",
            createdAt = Date(),
            status = "pending"
        )
    )

    // Helper function to create a Date for a specific day in the past
    fun createDate(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.time
    }

    // Sample data for UserStatisticsState
    val sampleUserStatisticsState = UserStatisticsState(
        timeframe = Timeframe.MONTH,
        userStats = listOf(
            UserStatPoint(label = "Day 1", count = 10, date = createDate(29)),  // 29 days ago
            UserStatPoint(label = "Day 2", count = 15, date = createDate(28)),
            UserStatPoint(label = "Day 3", count = 8, date = createDate(27)),
            UserStatPoint(label = "Day 4", count = 12, date = createDate(26)),
            UserStatPoint(label = "Day 5", count = 20, date = createDate(25)),
            UserStatPoint(label = "Day 6", count = 18, date = createDate(24)),
            UserStatPoint(label = "Day 7", count = 25, date = createDate(23)),
            UserStatPoint(label = "Day 8", count = 30, date = createDate(22)),
            UserStatPoint(label = "Day 9", count = 22, date = createDate(21)),
            UserStatPoint(label = "Day 10", count = 28, date = createDate(20)),
            UserStatPoint(label = "Day 11", count = 35, date = createDate(19)),
            UserStatPoint(label = "Day 12", count = 40, date = createDate(18)),
            UserStatPoint(label = "Day 13", count = 32, date = createDate(17)),
            UserStatPoint(label = "Day 14", count = 45, date = createDate(16)),
            UserStatPoint(label = "Day 15", count = 50, date = createDate(15)),
            UserStatPoint(label = "Day 16", count = 48, date = createDate(14)),
            UserStatPoint(label = "Day 17", count = 55, date = createDate(13)),
            UserStatPoint(label = "Day 18", count = 60, date = createDate(12)),
            UserStatPoint(label = "Day 19", count = 52, date = createDate(11)),
            UserStatPoint(label = "Day 20", count = 65, date = createDate(10)),
            UserStatPoint(label = "Day 21", count = 70, date = createDate(9)),
            UserStatPoint(label = "Day 22", count = 68, date = createDate(8)),
            UserStatPoint(label = "Day 23", count = 75, date = createDate(7)),
            UserStatPoint(label = "Day 24", count = 80, date = createDate(6)),
            UserStatPoint(label = "Day 25", count = 72, date = createDate(5)),
            UserStatPoint(label = "Day 26", count = 85, date = createDate(4)),
            UserStatPoint(label = "Day 27", count = 90, date = createDate(3)),
            UserStatPoint(label = "Day 28", count = 88, date = createDate(2)),
            UserStatPoint(label = "Day 29", count = 95, date = createDate(1)),
            UserStatPoint(label = "Day 30", count = 100, date = createDate(0))  // Today
        ),
        isLoading = false,
        error = null,
        startDate = createDate(29),  // 29 days ago (start of the 30-day period)
        endDate = createDate(0),     // Today
        totalUsers = 1500,           // Total users in the system
        newUsersInPeriod = 1250,     // Sum of counts in userStats (new users in the last 30 days)
        percentChange = 20.0f        // 20% increase in new users compared to the previous period
    )
}