package com.androidfinalproject.hacktok.ui.newPost

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewPostViewModel : ViewModel() {
    private val _state = MutableStateFlow(NewPostState())
    val state: StateFlow<NewPostState> = _state

    fun onAction(action: NewPostAction) {
        when (action) {
            is NewPostAction.UpdateCaption -> _state.value = _state.value.copy(caption = action.caption)
            is NewPostAction.UpdatePrivacy -> _state.value = _state.value.copy(privacy = action.privacy)
            is NewPostAction.SubmitPost -> {
                // Implement post logic here
                _state.value = _state.value.copy(isPosting = true)
            }
            else -> {

            }
        }
    }
}