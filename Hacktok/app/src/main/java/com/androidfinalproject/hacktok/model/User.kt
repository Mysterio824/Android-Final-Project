package com.androidfinalproject.hacktok.model

import com.androidfinalproject.hacktok.model.enums.UserRole
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class User(
    @PropertyName("id") val id: String? = null,
    @PropertyName("username") val username: String? = null,
    @PropertyName("email") val email: String,
    @PropertyName("profileImage") val profileImage: String? = null,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("active") val isActive: Boolean = true,
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
    @PropertyName("followingCount") val followingCount: Int = 0,
    @PropertyName("searchHistory") val searchHistory: List<String> = emptyList(),
    @PropertyName("banInfo") val banInfo: BanInfo? = null,
    val videosCount: Int = 0
) {
    // Constructor không tham số cho Firestore
    constructor() : this(
        null, null, "", null, Date(), true, UserRole.USER, null, null,
        PrivacySettings(), null, "en", emptyList(), emptyList(), emptyList(), emptyList(), 0, 0, emptyList()
    )

    companion object {
        fun fromFirebaseUser(firebaseUser: FirebaseUser): User {
            return User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                username = firebaseUser.displayName,
                profileImage = firebaseUser.photoUrl?.toString(),
                createdAt = Date(firebaseUser.metadata?.creationTimestamp ?: 0),
                isActive = true,
                role = if (firebaseUser.isEmailVerified) UserRole.ADMIN else UserRole.USER,
                bio = null,
                fullName = null,
                privacySettings = PrivacySettings(),
                lastActive = null,
                language = "en",
                friends = emptyList(),
                blockedUsers = emptyList(),
                followers = emptyList(),
                following = emptyList(),
                followerCount = 0,
                followingCount = 0,
                searchHistory = emptyList(),
                videosCount = 0,
            )
        }
    }

    fun isCurrentlyBanned(): Boolean {
        val now = Date()
        println("isBanned: ${banInfo?.isBanned}, endDate: ${banInfo?.endDate}, now: $now")
        return banInfo?.isBanned == true && (banInfo.endDate == null || now.before(banInfo.endDate))
    }
}