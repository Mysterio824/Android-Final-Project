package com.androidfinalproject.hacktok.ui.adminManage.reportManagement.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun BanUserDialog(
    userId: String,
    onDismiss: () -> Unit,
    onBanUser: (userId: String, isPermanent: Boolean, durationDays: Int?, reason: String) -> Unit
) {
    var isPermanent by remember { mutableStateOf(false) }
    var banDuration by remember { mutableStateOf("7") }
    var banReason by remember { mutableStateOf("") }

    var showDurationError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ban User") },
        text = {
            Column {
                Text("User ID: $userId")
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPermanent,
                        onCheckedChange = {
                            isPermanent = it
                            showDurationError = false // reset error on switch
                        }
                    )
                    Text("Permanent Ban")
                }

                if (!isPermanent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = banDuration,
                        onValueChange = {
                            banDuration = it
                            showDurationError = false
                        },
                        label = { Text("Ban Duration (days)") },
                        isError = showDurationError,
                        supportingText = {
                            if (showDurationError) {
                                Text("Please enter a valid number > 0")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = banReason,
                    onValueChange = { banReason = it },
                    label = { Text("Reason for Ban (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val durationValid = isPermanent || (banDuration.toIntOrNull()?.let { it > 0 } ?: false)
                    showDurationError = !isPermanent && !durationValid

                    if (durationValid) {
                        val duration = if (isPermanent) null else banDuration.toInt()
                        onBanUser(userId, isPermanent, duration, banReason.trim())
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