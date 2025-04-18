package com.androidfinalproject.hacktok.ui.currentProfile

import com.androidfinalproject.hacktok.model.Post

sealed class CurrentProfileAction {
    data class OnPostClick(val post: Post) : CurrentProfileAction()
    data class NavigateFriendList(val userId: String) : CurrentProfileAction()
    data class NavigateToPostEdit(val postId: String) : CurrentProfileAction()
    data object NavigateToProfileEdit : CurrentProfileAction()
    data object NavigateToNewPost : CurrentProfileAction()
    object OnNavigateBack : CurrentProfileAction()
    object OnEditProfile : CurrentProfileAction()
    object OnCreatePost : CurrentProfileAction()
    object RetryLoading : CurrentProfileAction()
    data class OnDeletePost(val postId: String) : CurrentProfileAction()
    data class OnUserClick(val userId: String) : CurrentProfileAction()
}