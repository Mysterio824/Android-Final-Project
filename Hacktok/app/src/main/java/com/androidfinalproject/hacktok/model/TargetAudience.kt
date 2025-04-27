package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class TargetAudience(
    @PropertyName("ageMin") val ageMin: Int = 13,
    @PropertyName("ageMax") val ageMax: Int = 65,
    @PropertyName("interests") val interests: List<String> = emptyList(),
    @PropertyName("locations") val locations: List<String> = emptyList()
) : Serializable {
    constructor() : this(13, 65, emptyList(), emptyList())
}