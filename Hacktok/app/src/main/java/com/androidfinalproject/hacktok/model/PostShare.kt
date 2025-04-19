package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class PostShare(
    @PropertyName("id") val id: String? = null,
    @PropertyName("postId") val postId: String, // ID of the original post
    @PropertyName("sharedBy") val sharedBy: String, // ID of the user who shared
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("post") val post: Post? = null, // Optional: for UI convenience
    @PropertyName("user") val user: User? = null, // Optional: the sharer’s info
    @PropertyName("privacy") val privacy: String = "",
    @PropertyName("content") val content: String = "",
) {
    constructor() : this(null, "", "", Date(), null, null)
}