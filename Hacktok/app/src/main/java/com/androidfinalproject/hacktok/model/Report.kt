package com.androidfinalproject.hacktok.model

// Import enums
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Report(
    @PropertyName("id") val id: String? = null,
    @PropertyName("reportedBy") val reportedBy: String = "",
    @PropertyName("type") private val _type: String? = null,
    @PropertyName("targetId") val targetId: String = "",
    @PropertyName("reason") private val _reason: String? = null,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("status") val status: String = "pending",
    @PropertyName("resolvedBy") val resolvedBy: String? = null,
    @PropertyName("resolutionNote") val resolutionNote: String? = null
) {
    val type: ReportType?
        get() = _type?.let { 
            ReportType.entries.find { enum -> enum.name.equals(it, ignoreCase = true) }
        }

    val reason: ReportCause?
        get() = _reason?.let { ReportCause.fromValue(it) }

    // Secondary constructor for creating new reports
    constructor(
        reportedBy: String,
        targetId: String,
        type: ReportType,
        reason: ReportCause
    ) : this(
        id = null,
        reportedBy = reportedBy,
        _type = type.name,
        targetId = targetId,
        _reason = reason.name,
        createdAt = Date(),
        status = "pending",
        resolvedBy = null,
        resolutionNote = null
    )
}