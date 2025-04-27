package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PostOptionsContent(
    isPostOwner: Boolean,
    isPostSaved: Boolean,
    onDismiss: () -> Unit,
    onReport: () -> Unit,
    onSavePost: () -> Unit,
    onUnsavePost: () -> Unit,
    onPostDelete: () -> Unit = {},
    onPostEdit: () -> Unit = {},
) {
    fun withDismiss(action: () -> Unit): () -> Unit = {
        action()
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Post Options",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        if (!isPostOwner) {
            OptionItem(
                title = "Report Post",
                icon = Icons.Default.Report,
                onClick = withDismiss(onReport)
            )
        }

        if (isPostSaved) {
            OptionItem(
                title = "Unsave Post",
                icon = Icons.Default.Save,
                onClick = withDismiss(onUnsavePost)
            )
        } else {
            OptionItem(
                title = "Save Post",
                icon = Icons.Default.Save,
                onClick = withDismiss(onSavePost)
            )
        }

        // Options only for post owner
        if (isPostOwner) {
            OptionItem(
                title = "Edit Post",
                icon = Icons.Default.Edit,
                onClick = withDismiss(onPostEdit)
            )

            OptionItem(
                title = "Delete Post",
                icon = Icons.Default.Delete,
                onClick = withDismiss(onPostDelete)
            )

//            OptionItem(
//                title = "Change Privacy",
//                icon = Icons.Default.Public,
//                onClick = onDismiss
//            )
        }
    }
}