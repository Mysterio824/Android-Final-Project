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
    @PropertyName("likeCount") val likeCount: Int = 0, // Số lượt thích
    @PropertyName("parentCommentId") val parentCommentId: String? = null, // Hỗ trợ bình luận lồng nhau
    @PropertyName("replyCount") val replyCount: Int = 0 // Số phản hồi cho bình luận này
) {
    // Constructor không tham số cho Firestore
    constructor() : this(null, "", "", "", Date(), false, 0, null, 0)

    override fun toString(): String {
        return "Comment(id=$id, content='$content', userId='$userId', postId='$postId', " +
                "createdAt=$createdAt, isReported=$isReported, likeCount=$likeCount, " +
                "parentCommentId=$parentCommentId, replyCount=$replyCount)"
    }
}