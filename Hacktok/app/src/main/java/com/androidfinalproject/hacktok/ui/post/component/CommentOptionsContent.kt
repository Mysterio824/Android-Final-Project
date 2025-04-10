package com.androidfinalproject.hacktok.ui.post.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CommentOptionsContent(
    commentId: String?,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Comment Options",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        OptionItem(
            title = "Copy Text",
            onClick = onDismiss
        )

        OptionItem(
            title = "Report Comment",
            description = "This comment concerns me",
            onClick = onDismiss
        )

        // If comment owner (would need to check in real app)
        // OptionItem(
        //     title = "Edit Comment",
        //     onClick = onDismiss
        // )
        //
        // OptionItem(
        //     title = "Delete Comment",
        //     onClick = onDismiss
        // )
    }
}
