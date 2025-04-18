package com.androidfinalproject.hacktok.ui.adminManage.reportManagement

import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.model.enums.ReportType

sealed class ReportManagementAction {
    data class BanUser(val userId: String, val isPermanent: Boolean, val durationDays: Int?) : ReportManagementAction()
    data class ResolveReport(val reportId: String, val resolutionNote: String) : ReportManagementAction()
    data class OpenBanUserDialog(val report: Report) : ReportManagementAction()
    object CloseBanUserDialog : ReportManagementAction()
    data class OpenResolveReportDialog(val report: Report) : ReportManagementAction()
    object CloseResolveReportDialog : ReportManagementAction()
    data class DeleteContent(val contentId: String, val contentType: ReportType) : ReportManagementAction()
    data class SetStatusFilter(val filter: StatusFilter) : ReportManagementAction() // New: Action to set filter
    object OpenFilterMenu : ReportManagementAction() // New: Action to open filter menu
    object CloseFilterMenu : ReportManagementAction() // New: Action to close filter menu
}