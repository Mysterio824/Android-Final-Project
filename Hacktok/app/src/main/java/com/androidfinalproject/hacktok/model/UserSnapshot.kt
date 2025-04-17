package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date


data class UserSnapshot(
    @PropertyName("username") val username: String = "",
    @PropertyName("profileImage") val profileImage: String? = null,
    @PropertyName("snapshotAt") val snapshotAt: Date = Date()
) {
    constructor() : this("", null, Date())
}