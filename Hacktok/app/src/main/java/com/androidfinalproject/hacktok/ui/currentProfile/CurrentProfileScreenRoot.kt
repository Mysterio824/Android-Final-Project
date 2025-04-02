package com.androidfinalproject.hacktok.ui.currentProfile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun CurrentProfileScreenRoot(
    navController: NavController,
    viewModel: CurrentProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Observe actions and handle navigation
    LaunchedEffect(Unit) {

    };

    CurrentProfileScreen(
        navController = navController,
        user = state.user,
        posts = state.posts,
        friendCount = state.friendCount,
        onPostEdit = {},
        onProfileEdit = {}
    )
}