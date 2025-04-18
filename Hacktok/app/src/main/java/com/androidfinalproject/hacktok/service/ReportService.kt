package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType

interface ReportService {
    /**
     * Submits a new report.
     * @param reportedItemId The ID of the item being reported (Post ID, Comment ID, User ID).
     * @param reportType The type of item being reported.
     * @param reportCause The reason for the report.
     * @throws Exception if the report submission fails.
     */
    suspend fun submitReport(
        reportedItemId: String,
        reportType: ReportType,
        reportCause: ReportCause
    ) : Boolean
} 