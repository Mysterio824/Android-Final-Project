package com.androidfinalproject.hacktok.model

import org.bson.types.ObjectId
import java.util.Date

data class Post(
    val id: ObjectId? = null,
    val content: String,
    val user: User,  // Tham chiếu đến người dùng đã tạo post
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    val likeCount: Int
)
