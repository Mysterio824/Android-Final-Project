package com.androidfinalproject.hacktok.ui.search

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

sealed class SearchAction {
    data class UpdateQuery(val query: String) : SearchAction()
    data class ChangeTab(val tabIndex: Int) : SearchAction()
    data class OnUserClick(val user: User) : SearchAction()
    data class OnPostClick(val post: Post) : SearchAction()
    object OnNavigateBack : SearchAction()
}