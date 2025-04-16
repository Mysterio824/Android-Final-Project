package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName

data class Media(
    @PropertyName("type") val type: String = "image", // "image", "video"
    @PropertyName("url") val url: String = "",
    @PropertyName("thumbnailUrl") val thumbnailUrl: String? = null
) {
    constructor() : this("image", "", null)
}