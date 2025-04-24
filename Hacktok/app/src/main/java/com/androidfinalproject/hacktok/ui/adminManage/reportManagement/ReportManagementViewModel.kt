package com.androidfinalproject.hacktok.ui.adminManage.reportManagement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.androidfinalproject.hacktok.repository.ReportRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReportManagementViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ReportManagementState())
    val state: StateFlow<ReportManagementState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val reports = reportRepository.getPendingReports()
            Log.d("REPORTS", reports.toString())
            val reportCounts = reports.groupingBy { "${it.type}:${it.targetId}" }.eachCount()
            _state.update { it.copy(reports = reports, reportCounts = reportCounts) }
        }
    }

    fun onAction(action: ReportManagementAction) {
        when (action) {
            is ReportManagementAction.BanUser -> {
                viewModelScope.launch {
                    val duration = if (action.isPermanent) Long.MAX_VALUE else action.durationDays?.toLong() ?: 0L

                    try {
                        // Step 1: Ban the user
                        userRepository.banUser(
                            userId = action.userId,
                            reason = action.reason,
                            duration = duration
                        )

                        // Step 2: Update reports with that targetId
                        val matchingReports = _state.value.reports.filter { it.targetId == action.userId }

                        matchingReports.forEach { report ->
                            reportRepository.updateReport(
                                report.id ?: return@forEach, // Skip if no ID
                                mapOf(
                                    "status" to "resolved",
                                    "resolutionNote" to "target user is banned",
                                    "resolvedAt" to Date()
                                )
                            )
                        }

                        // Step 3: Update local state
                        _state.update {
                            val updatedReports = it.reports.map { report ->
                                if (report.targetId == action.userId) {
                                    report.copy(
                                        status = "resolved"
                                    )
                                } else report
                            }

                            it.copy(
                                reports = updatedReports,
                                isBanUserDialogOpen = false,
                                selectedReport = null
                            )
                        }

                    } catch (e: Exception) {
                        Log.e("ReportViewModel", "Error banning user ${action.userId}", e)
                    }
                }
            }
            is ReportManagementAction.ResolveReport -> {
                viewModelScope.launch {
                    try {
                        reportRepository.updateReport(
                            action.reportId,
                            mapOf(
                                "status" to "resolved",
                                "resolutionNote" to action.resolutionNote,
                                "resolvedAt" to Date() // Optional: track when resolved
                            )
                        )

                        // 2. Update UI state
                        _state.update { it.copy(
                            reports = it.reports.map { report ->
                                if (report.id == action.reportId) {
                                    report.copy(status = "resolved") // update locally
                                } else report
                            },
                            isResolveReportDialogOpen = false,
                            selectedReport = null
                        ) }

                    } catch (e: Exception) {
                        // Optional: handle/report error
                        Log.e("ReportViewModel", "Error resolving report ${action.reportId}", e)
                    }
                }
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
//                _state.update { it.copy(
//                    reports = it.reports.filter { it.targetId != action.contentId || it.type != action.contentType }
//                ) }
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