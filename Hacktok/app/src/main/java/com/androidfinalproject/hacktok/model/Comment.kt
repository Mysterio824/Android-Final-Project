package com.androidfinalproject.hacktok.model

import org.bson.types.ObjectId

data class Comment (
    val id: ObjectId? = null,
    val comment: String,
    val like: Int,
    val user: User
)