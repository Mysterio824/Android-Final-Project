package com.androidfinalproject.hacktok.ui.createAd

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Ad
import com.androidfinalproject.hacktok.model.TargetAudience
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.AdType
import com.androidfinalproject.hacktok.repository.AdRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.ui.createAd.CreateAdState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateAdViewModel @Inject constructor(
    private val adRepository: AdRepository,
    private val authService: AuthService
) : ViewModel() {
    private val tag = "CreateAdViewModel"
    private val _state = MutableStateFlow(CreateAdState())
    val state: StateFlow<CreateAdState> = _state.asStateFlow()

    init {
        loadCurrentUser()
        updateEndDate(_state.value.durationDays)
        loadUserAds()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = authService.getCurrentUser()
                _state.update { it.copy(currentUser = user) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load user: ${e.message}") }
            }
        }
    }

    private fun updateEndDate(days: Int) {
        val currentTime = System.currentTimeMillis()
        val endDate = Date(currentTime + (days * 24 * 60 * 60 * 1000))
        _state.update { it.copy(endDate = endDate) }
    }

    private fun loadUserAds() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoadingAds = true, error = null) }
                val currentUser = _state.value.currentUser
                if (currentUser == null) {
                    _state.update { 
                        it.copy(
                            isLoadingAds = false,
                            error = "User not found. Please try again.",
                            userAds = emptyList()
                        )
                    }
                    return@launch
                }
                
                val userId = currentUser.id
                if (userId.isNullOrEmpty()) {
                    _state.update { 
                        it.copy(
                            isLoadingAds = false,
                            error = "Invalid user ID. Please try again.",
                            userAds = emptyList()
                        )
                    }
                    return@launch
                }

                val ads = adRepository.getUserAds(userId)
                _state.update { 
                    it.copy(
                        userAds = ads,
                        isLoadingAds = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error loading user ads", e)
                _state.update { 
                    it.copy(
                        error = "Failed to load ads: ${e.message}",
                        isLoadingAds = false,
                        userAds = emptyList()
                    )
                }
            }
        }
    }

    private fun deleteAd(adId: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isDeletingAd = true) }
                adRepository.deleteAd(adId)
                loadUserAds() // Reload ads after deletion
                _state.update { it.copy(isDeletingAd = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete ad: ${e.message}", isDeletingAd = false) }
            }
        }
    }

    fun onAction(action: CreateAdAction) {
        when (action) {
            is CreateAdAction.UpdateAdContent -> {
                _state.update { it.copy(adContent = action.content) }
            }
            is CreateAdAction.UpdateAdMedia -> {
                _state.update { it.copy(mediaUrl = action.url) }
            }
            is CreateAdAction.SelectAdType -> {
                _state.update { it.copy(adType = action.adType) }
            }
            is CreateAdAction.UpdateDuration -> {
                val endDate = Date(System.currentTimeMillis() + (action.days * 24 * 60 * 60 * 1000L))
                _state.update { it.copy(durationDays = action.days, endDate = endDate) }
            }
            is CreateAdAction.UpdateAgeRange -> {
                val currentAudience = _state.value.targetAudience
                _state.update { 
                    it.copy(targetAudience = currentAudience.copy(
                        ageMin = action.min,
                        ageMax = action.max
                    ))
                }
            }
            is CreateAdAction.AddInterest -> {
                val currentAudience = _state.value.targetAudience
                val newInterests = currentAudience.interests + action.interest
                _state.update { 
                    it.copy(targetAudience = currentAudience.copy(interests = newInterests))
                }
            }
            is CreateAdAction.RemoveInterest -> {
                val currentAudience = _state.value.targetAudience
                val newInterests = currentAudience.interests - action.interest
                _state.update { 
                    it.copy(targetAudience = currentAudience.copy(interests = newInterests))
                }
            }
            is CreateAdAction.AddLocation -> {
                val currentAudience = _state.value.targetAudience
                val newLocations = currentAudience.locations + action.location
                _state.update { 
                    it.copy(targetAudience = currentAudience.copy(locations = newLocations))
                }
            }
            is CreateAdAction.RemoveLocation -> {
                val currentAudience = _state.value.targetAudience
                val newLocations = currentAudience.locations - action.location
                _state.update { 
                    it.copy(targetAudience = currentAudience.copy(locations = newLocations))
                }
            }
            is CreateAdAction.SubmitAd -> {
                viewModelScope.launch {
                    _state.update { it.copy(isSubmitting = true, error = null) }
                    try {
                        val currentState = _state.value
                        val currentUser = currentState.currentUser

                        if (currentUser == null) {
                            _state.update { it.copy(
                                isSubmitting = false,
                                error = "User not authenticated. Please try again."
                            )}
                            return@launch
                        }

                        if (currentState.adContent.isBlank()) {
                            _state.update { it.copy(
                                isSubmitting = false,
                                error = "Ad content cannot be empty"
                            )}
                            return@launch
                        }

                        val userId = currentUser.id
                        if (userId.isNullOrEmpty()) {
                            _state.update { it.copy(
                                isSubmitting = false,
                                error = "Invalid user ID. Please try again."
                            )}
                            return@launch
                        }

                        val ad = Ad(
                            advertiserId = userId,
                            userId = userId,
                            content = currentState.adContent,
                            mediaUrl = currentState.mediaUrl,
                            targetAudience = currentState.targetAudience
                        )

                        // Create the ad in the repository
                        val adId = adRepository.createAd(ad, currentState.durationDays)
                        
                        // Reload the user's ads to show the new one
                        loadUserAds()
                        
                        _state.update { it.copy(
                            isSubmitting = false,
                            isSuccess = true,
                            adContent = "", // Clear the form
                            mediaUrl = "", // Clear the media
                            error = null
                        )}
                    } catch (e: Exception) {
                        Log.e(tag, "Error submitting ad", e)
                        _state.update {
                            it.copy(
                                isSubmitting = false,
                                error = "Failed to submit ad: ${e.message}"
                            )
                        }
                    }
                }
            }
            is CreateAdAction.NavigateBack -> {
                // Navigation is handled by the composable
            }

            is CreateAdAction.UpdateTargetAudience -> TODO()
            is CreateAdAction.LoadUserAds -> {
                loadUserAds()
            }
            is CreateAdAction.DeleteAd -> {
                deleteAd(action.adId)
            }
        }
    }
}