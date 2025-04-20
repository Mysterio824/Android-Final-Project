package com.androidfinalproject.hacktok.model

import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostDataType
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatPoint
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatisticsState
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.Timeframe
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.UserStatPoint
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.UserStatisticsState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

object MockData {

    val mockMessages = listOf(
        Message(id = "m1", senderId = "1", content = "Hello!", createdAt = Date()),
        Message(id = "m2", senderId = "2", content = "Hey there!", createdAt = Date(), isRead = true),
        Message(id = "m3", senderId = "3", content = "How are you?", createdAt = Date(), replyTo = "m1")
    )

    val mockUsers = listOf(
        User(
            id = "1",
            username = "johndoe",
            email = "john@example.com",
            fullName = "John Doe",
            profileImage = "https://randomuser.me/api/portraits/men/1.jpg"
        ),
        User(
            id = "2",
            username = "janedoe",
            email = "jane@example.com",
            fullName = "Jane Doe",
            profileImage = "https://randomuser.me/api/portraits/women/1.jpg"
        ),
        User(
            id = "3",
            username = "marksmith",
            email = "mark@example.com",
            fullName = "Mark Smith",
            profileImage = "https://randomuser.me/api/portraits/men/2.jpg"
        )
    )

    val mockPosts = listOf(
        Post("1", "Hello world!", "2", likedUserIds = emptyList(), user = mockUsers[2], imageLink = "https://scontent.fsgn2-7.fna.fbcdn.net/v/t39.30808-6/489891322_715880880792703_6312706882615243490_n.jpg?_nc_cat=1&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeFozlH7UrDEFkjxJb7sOuyQO8rnOqHE1AE7yuc6ocTUAUOeEh3iImWld92-Du6tFcL9CjWKqdIATKXhgt6E_Ii_&_nc_ohc=XCChQ-I7gCoQ7kNvwE5sDbh&_nc_oc=AdmsBGP86HC1m8Rn352XjvrHRXc5ubimARBUNjviXL2cxS0_4iEfyOqstGiC-xnX9KY&_nc_zt=23&_nc_ht=scontent.fsgn2-7.fna&_nc_gid=9jZ00bprKo-C7-cg_l1ZzA&oh=00_AfG15vnRZyFD8SOtEJGN7pypgugDNN5UbFnT0_jcLyytMA&oe=67FC77C1"),
        Post("2", "Learning Kotlin!", "2", likedUserIds = emptyList(), user = mockUsers[1]),
        Post("3", "Jetpack Compose is fun!", "2", likedUserIds = emptyList(),  user = mockUsers.first())
    )

    val mockComments = listOf(
        Comment(
            id = "comment1",
            content = "This is the first comment on this post!",
            userId = "user2",
            userSnapshot = UserSnapshot(
                username = "cool_user2",
                profileImage = "https://example.com/profiles/user2.jpg"
            ),
            postId = "post1",
            createdAt = Date(System.currentTimeMillis() - 3600000), // 1 hour ago
        ),
        Comment(
            id = "comment2",
            content = "This is the second comment",
            userId = "user3",
            userSnapshot = UserSnapshot(
                username = "funny_user3",
                profileImage = "https://example.com/profiles/user3.jpg"
            ),
            postId = "post1",
            createdAt = Date(System.currentTimeMillis() - 7200000), // 2 hours ago
        ),
        Comment(
            id = "comment3",
            content = "Same second comment for testing",
            userId = "user3",
            userSnapshot = UserSnapshot(
                username = "funny_user3",
                profileImage = "https://example.com/profiles/user3.jpg"
            ),
            postId = "post1",
            createdAt = Date(System.currentTimeMillis() - 7000000),
        ),
        Comment(
            id = "comment4",
            content = "Yet another second comment",
            userId = "user3",
            userSnapshot = UserSnapshot(
                username = "funny_user3",
                profileImage = "https://example.com/profiles/user3.jpg"
            ),
            postId = "post1",
            createdAt = Date(System.currentTimeMillis() - 6900000),
        ),
        Comment(
            id = "comment5",
            content = "Duplicated for stress testing",
            userId = "user3",
            userSnapshot = UserSnapshot(
                username = "funny_user3",
                profileImage = "https://example.com/profiles/user3.jpg"
            ),
            postId = "post1",
            createdAt = Date(System.currentTimeMillis() - 6800000),
        ),
        Comment(
            id = "comment6",
            content = "Another one from user3",
            userId = "user3",
            userSnapshot = UserSnapshot(
                username = "funny_user3",
                profileImage = "https://example.com/profiles/user3.jpg"
            ),
            postId = "post1",
            createdAt = Date(System.currentTimeMillis() - 6700000),
        ),
        Comment(
            id = "reply1",
            content = "This is a reply to the first comment",
            userId = "user4",
            userSnapshot = UserSnapshot(
                username = "reply_guy4",
                profileImage = "https://example.com/profiles/user4.jpg"
            ),
            postId = "post1",
            parentCommentId = "comment1",
            createdAt = Date(System.currentTimeMillis() - 1800000) // 30 mins ago
        ),
        Comment(
            id = "reply2",
            content = "Another reply to the first comment",
            userId = "user5",
            userSnapshot = UserSnapshot(
                username = "commentator5",
                profileImage = "https://example.com/profiles/user5.jpg"
            ),
            postId = "post1",
            parentCommentId = "comment1",
            createdAt = Date(System.currentTimeMillis() - 900000) // 15 mins ago
        )
    )

    fun getMockNotifications(count: Int = 10): List<Notification> {
        val notifications = mutableListOf<Notification>()

        // Generate random notifications
        repeat(count) { index ->
            val mockNotification = generateRandomNotification(index)
            notifications.add(mockNotification)
        }

        // Sort by date (newest first)
        return notifications.sortedByDescending { it.createdAt }
    }


    private fun generateRandomNotification(index: Int): Notification {
        // Generate a random notification type
        val types = NotificationType.entries.toTypedArray()
        val randomType = types[index % types.size]

        // Generate a random date within the last 7 days
        val randomTimeAgo = (index * 2 + (0..24).random()).toLong()
        val date = Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(randomTimeAgo))

        // Generate a random sender
        val sender = mockUsers[index % mockUsers.size]

        // Generate content based on type
        val content = when (randomType) {
            NotificationType.FRIEND_REQUEST -> "${sender.username} sent you a friend request"
            NotificationType.FRIEND_ACCEPT -> "${sender.username} accepted your friend request"
            NotificationType.POST_LIKE -> "${sender.username} liked your post"
            NotificationType.POST_COMMENT -> "${sender.username} commented on your post: \"${mockComments.random()}\""
            NotificationType.COMMENT_REPLY -> "${sender.username} replied to your comment: \"${mockComments.random()}\""
            NotificationType.COMMENT_LIKE -> "${sender.username} liked your comment"
            NotificationType.ADMIN_NOTIFICATION -> "Important: ${mockAdminMessages.random()}"
        }

        // Set a random read status (more recent ones are more likely to be unread)
        val isRead = index > 3 || (0..10).random() > 7

        return Notification(
            id = UUID.randomUUID().toString(),
            userId = "user123", // Current user ID
            type = randomType,
            senderId = sender.id,
            senderName = sender.username,
            senderImage = sender.profileImage,
            relatedId = if (randomType == NotificationType.FRIEND_REQUEST || randomType == NotificationType.FRIEND_ACCEPT)
                sender.id
            else
                "post${(1..100).random()}",
            content = content,
            createdAt = date,
            isRead = isRead,
            priority = if (randomType == NotificationType.ADMIN_NOTIFICATION) "high" else "normal"
        )
    }

    private val mockAdminMessages = listOf(
        "Your account has been verified",
        "New features have been added to the app",
        "Please update your privacy settings",
        "Your post has been featured in our weekly highlights",
        "Security update available",
        "Welcome to HackTok! Explore the app features.",
        "Your video is trending!"
    )

    // Added mockUserRoles
    val mockUserRoles = listOf(
        UserRole.USER,
        UserRole.MODERATOR,
        UserRole.ADMIN,
        UserRole.SUPER_ADMIN
    )

    val mockReport = emptyList<Report>()

    val mockRelations = mapOf(
        "1" to RelationInfo("1", RelationshipStatus.NONE),
        "2" to RelationInfo("2", RelationshipStatus.PENDING_OUTGOING),
        "3" to RelationInfo("3", RelationshipStatus.PENDING_INCOMING)
    )

    // Sample data for UserStatisticsState
    fun createMockUserStatisticsState(): UserStatisticsState {
        fun createDate(daysAgo: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            return calendar.time
        }

        return  UserStatisticsState(
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

    fun createMockPostStatisticsState(): PostStatisticsState {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val random = java.util.Random(1234)

        val endDate = Date()

        calendar.time = endDate
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        val startDate = calendar.time

        val postStats = mutableListOf<PostStatPoint>()
        val bannedPostStats = mutableListOf<PostStatPoint>()

        calendar.time = startDate

        while (calendar.time <= endDate) {
            val date = calendar.time
            val label = dateFormat.format(date)

            val postCount = 100 + random.nextInt(400)

            val bannedRatio = 0.03 + (random.nextDouble() * 0.02)
            val bannedCount = (postCount * bannedRatio).toInt()

            postStats.add(PostStatPoint(label, postCount, date))
            bannedPostStats.add(PostStatPoint(label, bannedCount, date))

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return PostStatisticsState(
            timeframe = com.androidfinalproject.hacktok.ui.statistic.postStatistic.Timeframe.MONTH,
            dataType = PostDataType.BOTH,
            postStats = postStats,
            bannedPostStats = bannedPostStats,
            isLoading = false,
            error = null,
            startDate = startDate,
            endDate = endDate,
            totalPosts = 12500,
            totalBannedPosts = 450,
            newPostsInPeriod = postStats.sumOf { it.count },
            newBannedPostsInPeriod = bannedPostStats.sumOf { it.count },
            postPercentChange = 8.5f,
            bannedPostPercentChange = -3.2f
        )
    }
}