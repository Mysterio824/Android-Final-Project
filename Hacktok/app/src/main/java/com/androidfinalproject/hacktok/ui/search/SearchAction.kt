package com.androidfinalproject.hacktok.ui.search

sealed class SearchAction {
    data class UpdateQuery(val query: String) : SearchAction()
    data class ChangeTab(val tabIndex: Int) : SearchAction()
}