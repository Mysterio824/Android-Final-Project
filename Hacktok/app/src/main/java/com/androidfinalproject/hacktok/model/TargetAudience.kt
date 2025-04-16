package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName

data class TargetAudience(
    @PropertyName("ageRange") val ageRange: List<Int> = listOf(18, 65),
    @PropertyName("location") val location: String? = null
) {
    constructor() : this(listOf(18, 65), null)
}