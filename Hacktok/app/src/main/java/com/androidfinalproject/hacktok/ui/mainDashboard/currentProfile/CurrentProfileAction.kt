package com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile

import com.androidfinalproject.hacktok.model.Post

sealed class CurrentProfileAction {
    data class OnPostClick(val post: Post) : CurrentProfileAction()
    data object NavigateFriendList : CurrentProfileAction()
    data class NavigateToPostEdit(val post: Post) : CurrentProfileAction()
    data object NavigateToProfileEdit : CurrentProfileAction()
}