package com.androidfinalproject.hacktok.model

import org.bson.types.ObjectId
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
}