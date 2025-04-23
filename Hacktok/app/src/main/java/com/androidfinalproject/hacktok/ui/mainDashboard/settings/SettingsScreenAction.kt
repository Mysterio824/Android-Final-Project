package com.androidfinalproject.hacktok.ui.mainDashboard.settings

sealed class SettingsScreenAction{
    object OnNavigateEdit : SettingsScreenAction()
    object OnLogout: SettingsScreenAction()
    object OnChangePassword: SettingsScreenAction()
    object OnNavigateBack: SettingsScreenAction()
    data class OnChangeLanguage(val language: String): SettingsScreenAction()
}