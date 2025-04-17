package com.androidfinalproject.hacktok.model

import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Story(
    @PropertyName("id") val id: String? = null,
    @PropertyName("userId") val userId: String = "",
    @PropertyName("userName") val userName: String = "",
    @PropertyName("userAvatar") val userAvatar: String? = null,
    @PropertyName("media") val media: Media = Media(),
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("expiresAt") val expiresAt: Date = Date(),
    @PropertyName("viewerIds") val viewerIds: List<String> = emptyList(),
    val privacy: PRIVACY = PRIVACY.PUBLIC // Thêm field privacy
) {
    constructor() : this(null, "", "", null, Media(), Date(), Date(), emptyList())
}