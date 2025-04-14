package com.androidfinalproject.hacktok.ui.newStory

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewStoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(NewStoryState())
    val state: StateFlow<NewStoryState> = _state

    fun onAction(action: NewStoryAction) {
        when (action) {
            is NewStoryAction.GoToImageEditor -> {
                // optionally update state or log
            }
            else -> {}
        }
    }
}