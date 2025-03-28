package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName

data class Settings(
    @PropertyName("id") val id: String? = null,
    @PropertyName("maxPostLength") val maxPostLength: Int = 280,
    @PropertyName("allowedFileTypes") val allowedFileTypes: List<String> = listOf("jpg", "png", "mp4"),
    @PropertyName("reportThreshold") val reportThreshold: Int = 5
) {
    constructor() : this(null, 280, listOf("jpg", "png", "mp4"), 5)

    override fun toString(): String {
        return "Settings(id=$id, maxPostLength=$maxPostLength, " +
                "allowedFileTypes=$allowedFileTypes, reportThreshold=$reportThreshold)"
    }
}