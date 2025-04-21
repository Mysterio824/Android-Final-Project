package com.androidfinalproject.hacktok.ui.messageDashboard.component

import android.provider.ContactsContract.CommonDataKinds.Relation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.ui.commonComponent.OptionItem


@Composable
fun ChatOptionsContent(
    onDelete: () -> Unit,
    onMute: () -> Unit,
    onBlock: () -> Unit,
    onUnblock: () -> Unit,
    onDismiss: () -> Unit,
    status: RelationshipStatus
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
        when(status){
            RelationshipStatus.BLOCKING -> {
                OptionItem(
                    title = "Unblock",
                    icon = Icons.Default.PersonOff,
                    onClick = withDismiss(onUnblock)
                )

                OptionItem(
                    title = "Mute",
                    icon = Icons.AutoMirrored.Filled.VolumeOff,
                    onClick = withDismiss(onMute)
                )
            }
            RelationshipStatus.BLOCKED -> {}
            else -> {
                OptionItem(
                    title = "Block",
                    icon = Icons.Default.Block,
                    onClick = withDismiss(onBlock)
                )

                OptionItem(
                    title = "Mute",
                    icon = Icons.AutoMirrored.Filled.VolumeOff,
                    onClick = withDismiss(onMute)
                )
            }
        }

        OptionItem(
            title = "Delete",
            icon = Icons.Default.Delete,
            onClick = withDismiss(onDelete)
        )
    }
}