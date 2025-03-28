package com.androidfinalproject.hacktok.model

import org.bson.types.ObjectId
import java.util.Date

// Enum for user roles
enum class UserRole {
    USER,       // Regular user
    MODERATOR,  // Content moderator
    ADMIN,      // Full platform administrator
    SUPER_ADMIN // Top-level system administrator
}

data class User(
    val id: ObjectId? = null,
    val username: String,
    val email: String,
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    val role: UserRole = UserRole.USER
)