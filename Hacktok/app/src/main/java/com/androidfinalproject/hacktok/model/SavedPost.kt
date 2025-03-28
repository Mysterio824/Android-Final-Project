package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class SavedPost(
    @PropertyName("id") val id: String? = null,
    @PropertyName("userId") val userId: String,
    @PropertyName("postId") val postId: String,
    @PropertyName("savedAt") val savedAt: Date = Date(),
    @PropertyName("category") val category: String? = null
) {
    constructor() : this(null, "", "", Date(), null)

    override fun toString(): String {
        return "SavedPost(id=$id, userId='$userId', postId='$postId', savedAt=$savedAt)"
    }
}