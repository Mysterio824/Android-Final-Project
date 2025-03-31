package com.androidfinalproject.hacktok.ui.adminManage.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Report
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportManagementTab(
    reports: List<Report>,
    reportCounts: Map<String, Int>,
    isBanDialogOpen: Boolean,
    isResolveDialogOpen: Boolean,
    selectedReport: Report?,
    onOpenBanDialog: (Report) -> Unit,
    onCloseBanDialog: () -> Unit,
    onOpenResolveDialog: (Report) -> Unit,
    onCloseResolveDialog: () -> Unit,
    onBanUser: (String, Boolean, Int?) -> Unit,
    onDeleteContent: (String, String) -> Unit,
    onResolveReport: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        items(reports) { report ->
            ReportItem(
                report = report,
                reportCount = reportCounts[getReportKey(report)] ?: 1,
                onBan = { onOpenBanDialog(report) },
                onDelete = { onDeleteContent(report.targetId, report.type) },
                onResolve = { onOpenResolveDialog(report) }
            )
        }
    }

    if (isBanDialogOpen && selectedReport != null) {
        BanUserDialog(
            userId = selectedReport.targetId,
            onDismiss = onCloseBanDialog,
            onBanUser = onBanUser
        )
    }

    if (isResolveDialogOpen && selectedReport != null) {
        ResolveReportDialog(
            reportId = selectedReport.id ?: "",
            onDismiss = onCloseResolveDialog,
            onResolve = onResolveReport
        )
    }
}

private fun getReportKey(report: Report): String {
    return "${report.type}:${report.targetId}"
}

@Composable
fun ReportItem(
    report: Report,
    reportCount: Int,
    onBan: () -> Unit,
    onDelete: () -> Unit,
    onResolve: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Report #${report.id?.takeLast(6) ?: "Unknown"}",
                    style = MaterialTheme.typography.titleMedium
                )

                AssistChip(
                    onClick = {}, // Non-interactive for now; can be removed if no action is needed
                    label = { Text(report.status.capitalize()) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (report.status) {
                            "pending" -> MaterialTheme.colorScheme.errorContainer
                            "resolved" -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Type: ${report.type.capitalize()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Target ID: ${report.targetId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Reported by: ${report.reportedBy}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Reason: ${report.reason}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Date: ${dateFormat.format(report.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (reportCount > 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Multiple Reports",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reported $reportCount times",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (report.status != "resolved") {
                    if (report.type == "user") {
                        Button(
                            onClick = onBan,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = "Ban User",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Ban User")
                        }
                    } else {
                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Content",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Delete ${report.type.capitalize()}")
                        }
                    }

                    Button(
                        onClick = onResolve,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Resolve Report",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Resolve")
                    }
                }
            }
        }
    }
}

@Composable
fun BanUserDialog(
    userId: String,
    onDismiss: () -> Unit,
    onBanUser: (String, Boolean, Int?) -> Unit
) {
    var isPermanent by remember { mutableStateOf(false) }
    var banDuration by remember { mutableStateOf("7") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ban User") },
        text = {
            Column {
                Text("User ID: $userId")
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isPermanent,
                        onCheckedChange = { isPermanent = it }
                    )
                    Text("Permanent Ban")
                }

                if (!isPermanent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = banDuration,
                        onValueChange = {
                            banDuration = it
                            showError = false
                        },
                        label = { Text("Ban Duration (days)") },
                        isError = showError,
                        supportingText = {
                            if (showError) {
                                Text("Please enter a valid number")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isPermanent) {
                        onBanUser(userId, true, null)
                    } else {
                        try {
                            val days = banDuration.toInt()
                            if (days > 0) {
                                onBanUser(userId, false, days)
                            } else {
                                showError = true
                            }
                        } catch (e: NumberFormatException) {
                            showError = true
                        }
                    }
                }
            ) {
                Text("Ban User")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ResolveReportDialog(
    reportId: String,
    onDismiss: () -> Unit,
    onResolve: (String, String) -> Unit
) {
    var resolutionNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Resolve Report") },
        text = {
            Column {
                Text("Are you sure you want to mark this report as resolved?")
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = resolutionNote,
                    onValueChange = { resolutionNote = it },
                    label = { Text("Resolution Note (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onResolve(reportId, resolutionNote) }
            ) {
                Text("Resolve")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Extension function to capitalize the first letter of a string
private fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}