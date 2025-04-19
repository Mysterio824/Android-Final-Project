package com.androidfinalproject.hacktok.ui.adminManage.reportManagement.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementAction
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReportItem(
    report: Report,
    reportCount: Int,
    onAction: (ReportManagementAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = modifier
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
                    onClick = {},
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
                        text = "Type: ${report.type!!.name.capitalize()}",
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
                    if (report.type == ReportType.User) {
                        Button(
                            onClick = { onAction(ReportManagementAction.OpenBanUserDialog(report)) },
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
                            onClick = { onAction(ReportManagementAction.DeleteContent(report.targetId, report.type!!)) },
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
                            Text("Delete ${report.type!!.name.capitalize()}")
                        }
                    }

                    Button(
                        onClick = { onAction(ReportManagementAction.OpenResolveReportDialog(report)) },
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

private fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}