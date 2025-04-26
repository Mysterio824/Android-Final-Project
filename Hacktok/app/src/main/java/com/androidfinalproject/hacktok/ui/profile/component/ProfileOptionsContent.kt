package com.androidfinalproject.hacktok.ui.profile.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.commonComponent.OptionItem


@Composable
fun ProfileOptionsContent(
    onDismiss: () -> Unit,
    report: () -> Unit,
    block: () -> Unit,
    unblock: () -> Unit,
    seeFriend: () -> Unit,
    isBlock: Boolean = false
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
        OptionItem(
            title = "Report",
            icon = Icons.Default.Report,
            onClick = withDismiss(report)
        )

        if(isBlock){
            OptionItem(
                title = "Unblock",
                icon = Icons.Default.LockOpen,
                onClick = withDismiss(unblock)
            )
        } else {
            OptionItem(
                title = "Block",
                icon = Icons.Default.Block,
                onClick = withDismiss(block)
            )
        }


        OptionItem(
            title = "See friendship",
            icon = Icons.Default.People,
            onClick = withDismiss(seeFriend)
        )
    }
}