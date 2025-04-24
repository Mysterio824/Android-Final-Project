package com.androidfinalproject.hacktok.ui.mainDashboard

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.utils.NotificationPermissionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authService: AuthService,
) : ViewModel() {
    private val TAG = "DashboardViewModel"
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init{
        viewModelScope.launch {
            val currentUser = authService.getCurrentUser()
            _state.update{
                it.copy(currentUser = currentUser, isLogout = (currentUser == null))
            }
        }
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.SelectTab -> changeTab(action.index)
            is DashboardAction.CheckNotificationPermission -> checkNotificationPermission(action.context, action.permissionLauncher)
            else -> {}
        }
    }

    private fun changeTab(tabIndex: String) {
        _state.update { currentState ->
            currentState.copy(selectedTab = tabIndex)
        }
    }

    private fun checkNotificationPermission(context: Context, permissionLauncher: ActivityResultLauncher<String>) {
        // Use the singleton to handle permission checking
        NotificationPermissionHandler.checkAndRequestNotificationPermission(
            context = context,
            permissionLauncher = permissionLauncher
        )
    }}
