package com.androidfinalproject.hacktok.ui.adminManage

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdminManagementViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminManagementState())
    val state: StateFlow<AdminManagementState> = _state.asStateFlow()

    fun onAction(action: AdminManagementAction) {
        when (action) {
            is AdminManagementAction.SelectTab -> {
                _state.update { currentState ->
                    Log.d("TAB", action.tabIndex)
                    currentState.copy(selectedTab = action.tabIndex)
                }
            }

            else -> {}
        }
    }
}