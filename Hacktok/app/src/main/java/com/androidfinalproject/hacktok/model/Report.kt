package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Report(
    @PropertyName("id") val id: String? = null,
    @PropertyName("reportedBy") val reportedBy: String,
    @PropertyName("type") val type: String, // "post", "user", "comment"
    @PropertyName("targetId") val targetId: String,
    @PropertyName("reason") val reason: String,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("status") val status: String = "pending", // "pending", "resolved"
    @PropertyName("resolvedBy") val resolvedBy: String? = null,
    @PropertyName("resolutionNote") val resolutionNote: String? = null
) {
    constructor() : this(null, "", "", "", "", Date(), "pending", null, null)

    override fun toString(): String {
        return "Report(id=$id, reportedBy='$reportedBy', type='$type', targetId='$targetId', " +
                "status='$status', createdAt=$createdAt)"
    }
}