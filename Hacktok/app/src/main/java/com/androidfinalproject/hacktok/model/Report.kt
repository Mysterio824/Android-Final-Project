package com.androidfinalproject.hacktok.model

// Import enums
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp // Use ServerTimestamp for automatic timestamping
import java.util.Date // Keep Date for @ServerTimestamp

data class Report(
    @PropertyName("id") val id: String? = null,
    @PropertyName("reportedBy") val reportedBy: String,
    @PropertyName("type") val type: ReportType? = null, // "post", "user", "comment"
    @PropertyName("targetId") val targetId: String,
    @PropertyName("reason") val reason: ReportCause? = null,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("status") val status: String = "pending", // "pending", "resolved"
    @PropertyName("resolvedBy") val resolvedBy: String? = null,
    @PropertyName("resolutionNote") val resolutionNote: String? = null
) {
    // Firestore requires a no-arg constructor
    constructor() : this(null, "", null, "", null, Date(), "pending", null, null)
}