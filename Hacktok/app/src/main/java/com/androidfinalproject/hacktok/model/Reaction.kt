package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Reaction(
    @PropertyName("id") val id: String? = null,
    @PropertyName("postId") val postId: String,
    @PropertyName("userId") val userId: String,
    @PropertyName("emoji") val emoji: String, // Ví dụ: "👍", "❤️"
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("reactionType") val reactionType: String = "like" // "like", "love", "haha", etc.
) {
    constructor() : this(null, "", "", "", Date(), "like")

    override fun toString(): String {
        return "Reaction(id=$id, postId='$postId', userId='$userId', emoji='$emoji', " +
                "createdAt=$createdAt, reactionType='$reactionType')"
    }
}