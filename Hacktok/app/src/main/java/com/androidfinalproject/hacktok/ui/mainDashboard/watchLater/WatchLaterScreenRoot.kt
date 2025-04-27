//package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@Composable
//fun WatchLaterScreenRoot(
//    viewModel: WatchLaterViewModel = hiltViewModel(),
//    onUserProfileNavigate: (String) -> Unit,
//    onPostClickNavigation: (String) -> Unit,
//) {
//    val state = viewModel.state.collectAsState().value
//
//    WatchLaterScreen(
//        state = state,
//        onAction = { action ->
//            when (action) {
//                is WatchLaterAction.OnUserClick
//                    -> onUserProfileNavigate(action.userId)
//
//                is WatchLaterAction.OnPostClick
//                    -> onPostClickNavigation(action.postId)
//
//                is WatchLaterAction.OnCommentClick
//                    -> onPostClickNavigation(action.postId)
//
//                else -> viewModel.onAction(action)
//            }
//        }
//    )
//}