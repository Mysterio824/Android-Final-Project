package com.androidfinalproject.hacktok.ui.currentProfile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.ui.mainDashboard.home.HomeScreenAction
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

@Composable
fun CurrentProfileScreenRoot(
    onPostClickNavigation: (String) -> Unit,
    onPostEditNavigation: (String) -> Unit,
    onNewPostNavigation: () -> Unit,
    onFriendListNavigation: (String) -> Unit,
    onProfileEditNavigation: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val viewModel: CurrentProfileViewModel = hiltViewModel()
    
    CurrentProfileScreen(
        onNavigateBack = onNavigateBack,
        onNavigateToEditProfile = onProfileEditNavigation,
        onNavigateToNewPost = onNewPostNavigation,
        onNavigateToEditPost = { post -> onPostEditNavigation(post.id ?: "") }
    )
}