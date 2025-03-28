package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Group(
    @PropertyName("id") val id: String? = null,
    @PropertyName("groupName") val groupName: String,
    @PropertyName("description") val description: String? = null,
    @PropertyName("creatorId") val creatorId: String,
    @PropertyName("members") val members: List<String>,
    @PropertyName("admins") val admins: List<String>,
    @PropertyName("isPublic") val isPublic: Boolean = true,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("coverImage") val coverImage: String? = null
) {
    constructor() : this(null, "", null, "", emptyList(), emptyList(), true, Date(), null)

    override fun toString(): String {
        return "Group(id=$id, groupName='$groupName', creatorId='$creatorId', " +
                "isPublic=$isPublic, createdAt=$createdAt)"
    }
}