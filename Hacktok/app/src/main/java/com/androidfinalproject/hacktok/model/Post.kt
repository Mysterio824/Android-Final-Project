package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Post(
    @PropertyName("id") val id: String? = null, // ID do Firestore sinh hoặc tự định nghĩa
    @PropertyName("content") val content: String,
    @PropertyName("userId") val userId: String, // Tham chiếu đến user trong collection "users"
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("active") val isActive: Boolean = true,
    @PropertyName("likeCount") val likeCount: Int = 0, // Số lượt thích
    @PropertyName("commentCount") val commentCount: Int = 0, // Số lượng bình luận
    @PropertyName("imageLink") val imageLink: String = "",
    @PropertyName("privacy") val privacy: String = "",
    @PropertyName("user") val user: User? = null
) {
    // Constructor không tham số cho Firestore
    constructor() : this(null, "", "", Date(), true, 0, 0, "", "", null)

    override fun toString(): String {
        return "Post(id=$id, content='$content', userId='$userId', createdAt=$createdAt, " +
                "isActive=$isActive, likeCount=$likeCount, commentCount=$commentCount)"
    }
}