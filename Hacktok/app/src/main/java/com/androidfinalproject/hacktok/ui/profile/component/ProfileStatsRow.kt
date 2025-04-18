package com.androidfinalproject.hacktok.ui.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androidfinalproject.hacktok.ui.currentProfile.component.StatColumn
import com.androidfinalproject.hacktok.ui.profile.UserProfileAction
import com.androidfinalproject.hacktok.ui.profile.UserProfileState

// Extracted Stats Row
@Composable
fun ProfileStatsRow(
    state: UserProfileState,
    onAction: (UserProfileAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatColumn(
            count = state.numberOfFriends,
            label = "Friends",
            onClick = { state.user?.id?.let { onAction(UserProfileAction.NavigateFriendList) } }
        )
        StatColumn(
            count = state.posts.size,
            label = "Posts",
            onClick = { /* Maybe scroll to posts? */ }
        )
    }
}