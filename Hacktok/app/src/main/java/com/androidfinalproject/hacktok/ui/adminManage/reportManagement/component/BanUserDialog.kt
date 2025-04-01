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