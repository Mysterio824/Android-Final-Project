@file:Suppress("DEPRECATION")

package com.androidfinalproject.hacktok.ui.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.ui.profile.UserProfileAction
import com.androidfinalproject.hacktok.ui.profile.UserProfileState


@Composable
fun ProfileActionButtons(
    state: UserProfileState,
    onAction: (UserProfileAction) -> Unit,
    showOption: () -> Unit,
    showResponse: () -> Unit,
    isOwnProfile: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Friend/Follow Button - Show based on relationship status
        when (state.relationshipInfo?.status) {
            RelationshipStatus.FRIENDS -> {
                ActionButton(
                    icon = Icons.Default.Person,
                    onClick = { onAction(UserProfileAction.Unfriend) },
                    text = "Friends",
                    modifier = Modifier.weight(1f)
                )
            }
            RelationshipStatus.PENDING_OUTGOING -> {
                ActionButton(
                    icon = Icons.Default.HourglassEmpty,
                    onClick = { onAction(UserProfileAction.CancelFriendRequest) },
                    text = "Pending",
                    modifier = Modifier.weight(1f)
                )
            }
            RelationshipStatus.PENDING_INCOMING -> {
                Button(
                    onClick = showResponse,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        "Response",
                        fontSize = 15.sp
                    )
                }
            }
            RelationshipStatus.BLOCKING -> {
                ActionButton(
                    icon = Icons.Default.Block,
                    onClick = { onAction(UserProfileAction.UnblockUser) },
                    text = "Unblock",
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                Button(
                    onClick = { onAction(UserProfileAction.SendFriendRequest) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(15.dp),
                ) {
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(16.dp))
                    Text(
                        "Add",
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Message Button - Always show for other users
        if (!isOwnProfile && state.relationshipInfo?.status != RelationshipStatus.BLOCKING) {
            ActionButton(
                icon = Icons.AutoMirrored.Filled.Message,
                onClick =  { onAction(UserProfileAction.MessageUser) },
                text = "Message",
                modifier = Modifier.weight(1f)
            )
        }

        // Block Button - Show for non-friends
        if (!isOwnProfile) {
            OutlinedButton(
                onClick = showOption,
                shape = RoundedCornerShape(15.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary, // Background
                    contentColor = MaterialTheme.colorScheme.onSecondary  // Text/Icon
                )
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize).size(16.dp)
                )
            }
        }

        Spacer(Modifier.size(5.dp))
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(15.dp),
        modifier = modifier
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = text,
            fontSize = 15.sp
        )
    }
}
