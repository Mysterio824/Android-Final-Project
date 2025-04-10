package com.androidfinalproject.hacktok.ui.adminManage.reportManagement

import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReportManagementViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReportManagementState())
    val state: StateFlow<ReportManagementState> = _state.asStateFlow()

    init {
        val reports = MockData.mockReport
        val reportCounts = reports.groupingBy { "${it.type}:${it.targetId}" }.eachCount()
        _state.update { it.copy(reports = reports, reportCounts = reportCounts) }
    }

    fun onAction(action: ReportManagementAction) {
        when (action) {
            is ReportManagementAction.BanUser -> {
                _state.update { it.copy(isBanUserDialogOpen = false, selectedReport = null) }
            }
            is ReportManagementAction.ResolveReport -> {
                _state.update { it.copy(
                    reports = it.reports.map { report ->
                        if (report.id == action.reportId) report.copy(status = "resolved") else report
                    },
                    isResolveReportDialogOpen = false,
                    selectedReport = null
                ) }
            }
            is ReportManagementAction.OpenBanUserDialog -> {
                _state.update { it.copy(isBanUserDialogOpen = true, selectedReport = action.report) }
            }
            ReportManagementAction.CloseBanUserDialog -> {
                _state.update { it.copy(isBanUserDialogOpen = false, selectedReport = null) }
            }
            is ReportManagementAction.OpenResolveReportDialog -> {
                _state.update { it.copy(isResolveReportDialogOpen = true, selectedReport = action.report) }
            }
            ReportManagementAction.CloseResolveReportDialog -> {
                _state.update { it.copy(isResolveReportDialogOpen = false, selectedReport = null) }
            }
            is ReportManagementAction.DeleteContent -> {
                _state.update { it.copy(
                    reports = it.reports.filter { it.targetId != action.contentId || it.type != action.contentType }
                ) }
            }
            is ReportManagementAction.SetStatusFilter -> {
                _state.update { it.copy(statusFilter = action.filter) }
            }
            ReportManagementAction.OpenFilterMenu -> {
                _state.update { it.copy(isFilterMenuExpanded = true) }
            }
            ReportManagementAction.CloseFilterMenu -> {
                _state.update { it.copy(isFilterMenuExpanded = false) }
            }
        }
    }
}