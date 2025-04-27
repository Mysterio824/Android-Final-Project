package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Post(
    @PropertyName("id") val id: String? = null, // ID do Firestore sinh hoặc tự định nghĩa
    @PropertyName("content") val content: String,
    @PropertyName("userId") val userId: String, // Tham chiếu đến user trong collection "users"
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("active") val isActive: Boolean = true,
    @PropertyName("likedUserIds") val likedUserIds: List<Reaction> = emptyList(),
    @PropertyName("commentCount") val commentCount: Int = 0, // Số lượng bình luận
    @PropertyName("imageLink") val imageLink: String = "",
    @PropertyName("privacy") val privacy: String = "",
    @PropertyName("refPostId") val refPostId: String? = null,
) {
    fun getLikeCount(): Int = likedUserIds.size

    fun getEmoji(userId: String): String? =
        likedUserIds.find { it.userId == userId }?.emoji

    fun getTopEmojis(limit: Int = 3): List<String> {
        val emojiCounts = likedUserIds.groupBy { it.emoji }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }

        return emojiCounts
    }

    constructor() : this(null, "", "", Date(), true, emptyList(), 0, "", "", null)

    override fun toString(): String {
        return "Post(id=$id, content='$content', userId='$userId', createdAt=$createdAt, " +
                "isActive=$isActive, likeCount=${getLikeCount()}, commentCount=$commentCount)"
    }
}