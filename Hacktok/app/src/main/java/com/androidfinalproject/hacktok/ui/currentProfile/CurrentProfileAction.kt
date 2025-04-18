package com.androidfinalproject.hacktok.ui.currentProfile

import com.androidfinalproject.hacktok.model.Post

sealed class CurrentProfileAction {
    data class OnPostClick(val post: Post) : CurrentProfileAction()
    data object NavigateFriendList : CurrentProfileAction()
    data class NavigateToPostEdit(val post: Post) : CurrentProfileAction()
    data object NavigateToProfileEdit : CurrentProfileAction()
    data object NavigateToNewPost : CurrentProfileAction()
    object OnNavigateBack : CurrentProfileAction()
    object OnEditProfile : CurrentProfileAction()
    object OnCreatePost : CurrentProfileAction()
    object RetryLoading : CurrentProfileAction()
    data class OnEditPost(val post: Post, val newContent: String) : CurrentProfileAction()
    data class OnDeletePost(val post: Post) : CurrentProfileAction()
}