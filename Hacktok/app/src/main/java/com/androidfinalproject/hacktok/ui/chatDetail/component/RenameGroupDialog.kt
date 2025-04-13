package com.androidfinalproject.hacktok.ui.chatDetail.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RenameGroupDialog(
    initialGroupName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newGroupName by remember { mutableStateOf(initialGroupName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đổi tên nhóm") },
        text = {
            TextField(
                value = newGroupName,
                onValueChange = { newGroupName = it },
                label = { Text("Tên nhóm mới") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newGroupName) }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Hủy")
            }
        }
    )
}

@Preview
@Composable
fun RenameGroupDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        RenameGroupDialog(
            initialGroupName = "Nhóm Preview",
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false }
        )
    }
}