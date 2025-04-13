package com.androidfinalproject.hacktok.ui.chatDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatDetailViewModelFactory(
    private val chatId: String,
    private val isGroup: Boolean
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            val savedStateHandle = SavedStateHandle().apply {
                set("chatId", chatId)
                set("isGroup", isGroup)
            }
            return ChatDetailViewModel(savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}