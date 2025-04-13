package com.androidfinalproject.hacktok.ui.chatDetail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RenameGroupDialog(
    initialGroupName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newGroupName by remember { mutableStateOf(initialGroupName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Đổi tên nhóm",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            OutlinedTextField(
                value = newGroupName,
                onValueChange = { newGroupName = it },
                label = { Text("Tên nhóm mới") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF72BF6A),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF72BF6A),
                    cursorColor = Color(0xFF72BF6A)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newGroupName) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF72BF6A)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "Lưu",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Hủy")
            }
        },
        containerColor = Color.White,
        titleContentColor = Color.Black,
        shape = MaterialTheme.shapes.medium
    )
}