package com.androidfinalproject.hacktok.ui.mainDashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.FcmService
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
    private val fcmService: FcmService
) : ViewModel() {
    private val TAG = "DashboardViewModel"
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.SelectTab -> changeTab(action.index)
            is DashboardAction.OnLogout -> logout()
            else -> {}
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try{
                Log.d(TAG, "Logout action started")
                val status = authService.logout()
                if(!status) {
                    Log.e(TAG, "AuthService logout failed")
                } else{
                    Log.d(TAG, "AuthService logout successful, updating state")
                    fcmService.removeFcmToken()
                    // State update will trigger LaunchedEffect in Root
                    _state.update {
                        it.copy(
                            isLogout = true // Ensure loading is false after logout attempt
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout process: ${e.message}", e)
            }
        }
    }

    private fun changeTab(tabIndex: String) {
        _state.update { currentState ->
            currentState.copy(selectedTab = tabIndex)
        }
    }
}
