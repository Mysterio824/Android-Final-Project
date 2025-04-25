package com.androidfinalproject.hacktok.ui.profile.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.commonComponent.OptionItem


@Composable
fun ResponseOptionsContent(
    onDismiss: () -> Unit,
    accept: () -> Unit,
    unaccepted: () -> Unit,
) {
    val context = LocalContext.current

    fun withDismiss(action: () -> Unit): () -> Unit = {
        onDismiss()
        action()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        OptionItem(
            title = "Accept",
            icon = Icons.Default.Check,
            onClick = withDismiss(accept)
        )

        OptionItem(
            title = "Delete Request",
            icon = Icons.Default.Delete,
            onClick = withDismiss(unaccepted)
        )
    }
}