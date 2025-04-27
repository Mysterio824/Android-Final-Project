package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Comment(
    @PropertyName("id") val id: String? = null,
    @PropertyName("content") val content: String,
    @PropertyName("userId") val userId: String,
    @PropertyName("postId") val postId: String,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("isReported") val isReported: Boolean = false, // Trạng thái báo cáo
    @PropertyName("userSnapshot") val userSnapshot: UserSnapshot,
    @PropertyName("likedUserIds") val likedUserIds: List<Reaction> = emptyList(),
    @PropertyName("parentCommentId") val parentCommentId: String? = null, // Hỗ trợ bình luận lồng nhau
    @PropertyName("replyCount") val replyCount: Int = 0, // Số phản hồi cho bình luận này
    @PropertyName("updatedAt") val updatedAt: Long? = null,
    @PropertyName("isEdited") val isEdited: Boolean = false,
    @PropertyName("isDeleted") val isDeleted: Boolean = false
) {
    // Constructor không tham số cho Firestore
    constructor() : this(null, "", "", "", Date(), false,
        UserSnapshot(),
        emptyList(), null, 0, null, false, false)

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

    override fun toString(): String {
        return "Comment(id=$id, content='$content', userId='$userId', postId='$postId', " +
                "createdAt=$createdAt, isReported=$isReported, likeCount=${getLikeCount()}, " +
                "parentCommentId=$parentCommentId, replyCount=$replyCount, updatedAt=$updatedAt, " +
                "isEdited=$isEdited, isDeleted=$isDeleted)"
    }
}