package com.androidfinalproject.hacktok.ui.currentProfile

import com.androidfinalproject.hacktok.model.Post

sealed class CurrentProfileAction {
    data class NavigateToPostEdit(val post: Post) : CurrentProfileAction()
    data object NavigateToProfileEdit : CurrentProfileAction()
}