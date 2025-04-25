package com.androidfinalproject.hacktok.ui.mainDashboard.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigationBack: () -> Unit = {},
    onEditProfileNavigate: () -> Unit,
    onChangePasswordNavigate: () -> Unit,
    onCurrentProfileNavigate: () -> Unit,
    onAuthNavigate: () -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLogout) {
        if (state.isLogout)
            onAuthNavigate()
    }

    SettingsScreen(
        state = state,
        onAction = {action ->
            when(action){
                is SettingsScreenAction.OnNavigateBack -> onNavigationBack()
                is SettingsScreenAction.OnNavigateEdit -> onEditProfileNavigate()
                is SettingsScreenAction.OnChangePassword -> onChangePasswordNavigate()
                is SettingsScreenAction.OnCurrentProfileNavigate -> onCurrentProfileNavigate()
                else -> viewModel.onAction(action)
            }
        }
    )
}