package com.androidfinalproject.hacktok.ui.mainDashboard.settings

import android.app.Activity
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
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authService: AuthService,
    private val fcmService: FcmService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val TAG = "SettingsViewModel"

    private val _state = MutableStateFlow(SettingsState())
    val state : StateFlow<SettingsState> = _state.asStateFlow()

    fun onAction(action: SettingsScreenAction){
        when(action){
            is SettingsScreenAction.OnChangeLanguage -> changeLanguage(action.language)
            is SettingsScreenAction.OnLogout -> logout()
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
                            isLogout = true
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout process: ${e.message}", e)
            }
        }
    }

    private fun changeLanguage(language: String) {
//        val localeList = LocaleListCompat.forLanguageTags(language)
//        AppCompatDelegate.setApplicationLocales(localeList)
//
//        saveLanguagePreference(language)

        // Emit a state change to notify the UI to recreate the activity
        _state.update {
            it.copy(isLanguageChanged = true, language = language)
        }
    }

    private fun saveLanguagePreference(language: String) {
        // Example with SharedPreferences
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", language).apply()
    }
}