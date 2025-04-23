package com.androidfinalproject.hacktok.ui.mainDashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.service.AuthService
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
            _state.update{
                it.copy(currentUser = authService.getCurrentUser())
            }
        }
    }

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
