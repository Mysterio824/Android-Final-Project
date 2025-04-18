package com.androidfinalproject.hacktok.service.impl

import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.repository.ReportRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.ReportService
import javax.inject.Inject

class ReportServiceImpl @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authService: AuthService
) : ReportService {

    override suspend fun submitReport(
        reportedItemId: String,
        reportType: ReportType,
        reportCause: ReportCause
    ) : Boolean {
        try {
            val report = Report(
                reportedBy = authService.getCurrentUserId()!!,
                targetId = reportedItemId,
                type = reportType,
                reason = reportCause,
            )
            return reportRepository.addReport(report) != ""
        } catch (e: Exception) {
            throw Exception("Failed to submit report: ${e.message}", e)
        }
    }
}
