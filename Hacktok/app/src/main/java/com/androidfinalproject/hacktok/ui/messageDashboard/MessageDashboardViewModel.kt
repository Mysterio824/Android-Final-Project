package com.androidfinalproject.hacktok.ui.messageDashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageDashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(MessageDashboardState())
    val state = _state.asStateFlow()

    init {

    }

    fun onAction(action: MessageDashboardAction) {

    }
}