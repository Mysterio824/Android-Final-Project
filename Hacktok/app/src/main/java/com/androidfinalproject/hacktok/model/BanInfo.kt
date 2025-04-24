package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class BanInfo(
    @PropertyName("isBanned") val isBanned: Boolean = false,
    @PropertyName("reason") val reason: String? = null,
    @PropertyName("startDate") val startDate: Date? = null,
    @PropertyName("endDate") val endDate: Date? = null, // null means permanent
) {
    constructor() : this(false, null, null, null)
}
