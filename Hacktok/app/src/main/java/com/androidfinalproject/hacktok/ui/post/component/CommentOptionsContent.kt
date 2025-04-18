package com.androidfinalproject.hacktok.ui.post.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.ui.commonComponent.OptionItem


@Composable
fun CommentOptionsContent(
    comment: Comment,
    onDismiss: () -> Unit,
    reportComment: () -> Unit,
    deleteComment: () -> Unit,
    isCommentOwner: Boolean = false
) {
    val context = LocalContext.current

    fun withDismiss(action: () -> Unit): () -> Unit = {
        action()
        onDismiss()
    }

    val copyTextAction = {
        comment.content.let { text ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Comment", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Comment copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

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
            onClick = withDismiss(copyTextAction)
        )

        OptionItem(
            title = "Report Comment",
            description = "This comment concerns me",
            onClick = withDismiss(reportComment)
        )

        if(isCommentOwner){
             OptionItem(
                 title = "Delete Comment",
                    onClick = withDismiss(deleteComment)
             )
        }

    }
}