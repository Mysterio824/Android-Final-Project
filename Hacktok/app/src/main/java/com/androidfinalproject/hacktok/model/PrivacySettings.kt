package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName

data class PrivacySettings(
    @PropertyName("profileVisibility") val profileVisibility: String = "public",
    @PropertyName("postVisibility") val postVisibility: String = "public",
    @PropertyName("allowMessagesFrom") val allowMessagesFrom: String = "everyone"
)