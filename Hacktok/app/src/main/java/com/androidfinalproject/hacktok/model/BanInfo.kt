package com.androidfinalproject.hacktok.model

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import java.util.Date

@Keep // <- Helps Firestore + ProGuard (especially in release builds)
data class BanInfo(

    @get:PropertyName("isBanned")
    @set:PropertyName("isBanned")
    var isBanned: Boolean = false,

    @get:PropertyName("reason")
    @set:PropertyName("reason")
    var reason: String? = null,

    @get:PropertyName("startDate")
    @set:PropertyName("startDate")
    var startDate: Date? = null,

    @get:PropertyName("endDate")
    @set:PropertyName("endDate")
    var endDate: Date? = null // null means permanent
) {
    constructor() : this(false, null, null, null)
}