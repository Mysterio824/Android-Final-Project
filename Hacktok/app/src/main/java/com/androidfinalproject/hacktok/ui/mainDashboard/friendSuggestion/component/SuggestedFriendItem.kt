package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.RelationshipStatus
import com.androidfinalproject.hacktok.model.User

@Composable
fun SuggestedFriendItem(
    user: User,
    relation: RelationInfo,
    onSendRequest: () -> Unit,
    onUnSendRequest: () -> Unit,
    onRemove: () -> Unit,
    onUserClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable(onClick = onUserClick)
        ) {
            val imageUrl = user.profileImage
            val painter = rememberAsyncImagePainter(
                model = imageUrl.takeIf { !it.isNullOrBlank() },
                error = painterResource(id = R.drawable.placeholder_profile),
                placeholder = painterResource(id = R.drawable.placeholder_profile),
                fallback = painterResource(id = R.drawable.placeholder_profile)
            )

            Image(
                painter = painter,
                contentDescription = "Profile picture of ${user.username}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.fullName ?: user.username,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onUserClick)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (relation.status) {
                RelationshipStatus.PENDING_OUTGOING -> {
                    OutlinedButton(
                        onClick = onUnSendRequest,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel Request")
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onSendRequest,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Friend")
                        }

                        Button(
                            onClick = onRemove,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                           Text("Remove")
                        }
                    }
                }
            }
        }
    }
}