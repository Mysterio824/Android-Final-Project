package com.androidfinalproject.hacktok.model

import org.bson.types.ObjectId
import java.util.Date

data class User(
    val id: ObjectId? = null,
    val username: String,
    val email: String,
    val createdAt: Date = Date(),
    val isActive: Boolean = true
) {
    override fun toString(): String {
        return "id: $id, username: $username\n"
    }
}