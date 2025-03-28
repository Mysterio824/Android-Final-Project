package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

// Enum for user roles
enum class UserRole {
    USER,       // Regular user
    MODERATOR,  // Content moderator
    ADMIN,      // Full platform administrator
    SUPER_ADMIN // Top-level system administrator
}

data class User(
    @PropertyName("id") val id: String? = null,
    @PropertyName("username") val username: String,
    @PropertyName("email") val email: String,
    @PropertyName("profileImage") val profileImage: String? = null,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("isActive") val isActive: Boolean = true,
    @PropertyName("role") val role: UserRole = UserRole.USER,
    @PropertyName("bio") val bio: String? = null,
    @PropertyName("fullName") val fullName: String? = null,
    @PropertyName("privacySettings") val privacySettings: PrivacySettings = PrivacySettings(),
    @PropertyName("lastActive") val lastActive: Date? = null,
    @PropertyName("language") val language: String = "en",
    @PropertyName("friends") val friends: List<String> = emptyList(),
    @PropertyName("blockedUsers") val blockedUsers: List<String> = emptyList(),
    @PropertyName("followers") val followers: List<String> = emptyList(),
    @PropertyName("following") val following: List<String> = emptyList(),
    @PropertyName("followerCount") val followerCount: Int = 0,
    @PropertyName("followingCount") val followingCount: Int = 0
) {
    // Constructor không tham số cho Firestore
    constructor() : this(
        null, "", "", null, Date(), true, UserRole.USER, null, null,
        PrivacySettings(), null, "en", emptyList(), emptyList(), emptyList(), emptyList(), 0, 0
    )
}

data class PrivacySettings(
    @PropertyName("profileVisibility") val profileVisibility: String = "public",
    @PropertyName("postVisibility") val postVisibility: String = "public",
    @PropertyName("allowMessagesFrom") val allowMessagesFrom: String = "everyone"
)