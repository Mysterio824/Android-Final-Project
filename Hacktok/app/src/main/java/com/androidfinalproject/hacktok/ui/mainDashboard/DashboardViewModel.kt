package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.MessageDashboardAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import java.util.Date


class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.SelectTab -> changeTab(action.index)
            else -> {}
        }
    }

    private fun changeTab(tabIndex: String) {
        _state.update { currentState ->
            currentState.copy(selectedTab = tabIndex)
        }
    }
}
