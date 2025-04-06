package com.androidfinalproject.hacktok.ui.adminManage.reportManagement

import com.androidfinalproject.hacktok.model.Report

data class ReportManagementState(
    val reports: List<Report> = emptyList(),
    val reportCounts: Map<String, Int> = emptyMap(),
    val isBanUserDialogOpen: Boolean = false,
    val isResolveReportDialogOpen: Boolean = false,
    val selectedReport: Report? = null,
    val statusFilter: StatusFilter = StatusFilter.ALL, // New: Track filter in state
    val isFilterMenuExpanded: Boolean = false // New: Track menu state
) {
    // Compute filtered reports in the state
    val filteredReports: List<Report>
        get() = reports.filter { report ->
            when (statusFilter) {
                StatusFilter.ALL -> true
                StatusFilter.PENDING -> report.status == "pending"
                StatusFilter.RESOLVED -> report.status == "resolved"
            }
        }
}