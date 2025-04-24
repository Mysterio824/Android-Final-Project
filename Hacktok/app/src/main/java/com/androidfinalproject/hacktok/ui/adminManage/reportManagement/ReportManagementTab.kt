package com.androidfinalproject.hacktok.ui.adminManage.reportManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.component.*

enum class StatusFilter {
    ALL, PENDING, RESOLVED
}

@Composable
fun ReportManagementTab(
    state: ReportManagementState,
    onAction: (ReportManagementAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(onClick = { onAction(ReportManagementAction.OpenFilterMenu) }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }

                DropdownMenu(
                    expanded = state.isFilterMenuExpanded,
                    onDismissRequest = { onAction(ReportManagementAction.CloseFilterMenu) }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            onAction(ReportManagementAction.SetStatusFilter(StatusFilter.ALL))
                            onAction(ReportManagementAction.CloseFilterMenu)
                        },
                        trailingIcon = {
                            if (state.statusFilter == StatusFilter.ALL) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Pending") },
                        onClick = {
                            onAction(ReportManagementAction.SetStatusFilter(StatusFilter.PENDING))
                            onAction(ReportManagementAction.CloseFilterMenu)
                        },
                        trailingIcon = {
                            if (state.statusFilter == StatusFilter.PENDING) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Resolved") },
                        onClick = {
                            onAction(ReportManagementAction.SetStatusFilter(StatusFilter.RESOLVED))
                            onAction(ReportManagementAction.CloseFilterMenu)
                        },
                        trailingIcon = {
                            if (state.statusFilter == StatusFilter.RESOLVED) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )
                }
            }
        }

        // Report list
        if (state.filteredReports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reports match the current filters", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                items(state.filteredReports) { report ->
                    ReportItem(
                        report = report,
                        reportCount = state.reportCounts["${report.type}:${report.targetId}"] ?: 1,
                        onAction = { action -> onAction(action) }
                    )
                }
            }
        }
    }

    // Ban User Dialog
    if (state.isBanUserDialogOpen && state.selectedReport != null) {
        BanUserDialog(
            userId = state.selectedReport.targetId,
            onDismiss = { onAction(ReportManagementAction.CloseBanUserDialog) },
            onBanUser = { userId, isPermanent, durationDays, reason ->
                onAction(ReportManagementAction.BanUser(userId, isPermanent, durationDays, reason))
            }
        )
    }

    // Resolve Report Dialog
    if (state.isResolveReportDialogOpen && state.selectedReport != null) {
        ResolveReportDialog(
            reportId = state.selectedReport.id ?: "",
            onDismiss = { onAction(ReportManagementAction.CloseResolveReportDialog) },
            onResolve = { reportId, resolutionNote ->
                onAction(ReportManagementAction.ResolveReport(reportId, resolutionNote))
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportManagementTabPreview() {
    ReportManagementTab(
        state = ReportManagementState(),
        onAction = {}
    )
}
