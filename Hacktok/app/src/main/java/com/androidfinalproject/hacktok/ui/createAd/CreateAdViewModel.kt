package com.androidfinalproject.hacktok.ui.createAd

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateAdViewModel @Inject constructor(
    private val adRepository: AdRepository,
    private val authService: AuthService,
    application: Application
) : AndroidViewModel(application) {
    private val tag = "CreateAdViewModel"
    private val _state = MutableStateFlow(CreateAdState())
    val state: StateFlow<CreateAdState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val user = loadCurrentUser()
                if (user != null) {
                    updateEndDate(_state.value.durationDays)
                    loadUserAds()
                } else {
                    _state.update { it.copy(error = "Failed to load user. Please try again.") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to initialize: ${e.message}") }
            }
        }
    }

    private suspend fun loadCurrentUser(): User? {
        return try {
            val user = authService.getCurrentUser()
            Log.d("CREATEADDDDD", "${user?.id}")
            _state.update { it.copy(currentUser = user) }
            user
        } catch (e: Exception) {
            _state.update { it.copy(error = "Failed to load user: ${e.message}") }
            null
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
                Log.d("CREATE", "${currentUser?.id}")
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
            is CreateAdAction.UpdateAdUrl -> {
                _state.update { it.copy(url = action.url) }
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
                    try {
                        _state.update { it.copy(isSubmitting = true, error = null) }
                        val currentState = _state.value
                        val currentUser = currentState.currentUser

                        if (currentUser == null) {
                            _state.update { it.copy(
                                isSubmitting = false,
                                error = "User not found. Please try again."
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
                            targetAudience = currentState.targetAudience,
                            url = currentState.url
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
                            url = "", // Clear the URL
                            error = null
                        )}
                    } catch (e: Exception) {
                        _state.update { it.copy(
                            isSubmitting = false,
                            error = "Failed to create ad: ${e.message}"
                        )}
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
            is CreateAdAction.UploadImage -> {
                uploadImage(action.imageUri)
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val context = getApplication<Application>().applicationContext
                val tempFile = copyUriToTempFile(context, imageUri)

                if (tempFile == null || !tempFile.exists()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to process image"
                        )
                    }
                    return@launch
                }

                val imageUrl = uploadToCloudinary(tempFile)
                tempFile.delete()

                if (imageUrl.isNullOrBlank()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to upload image"
                        )
                    }
                    return@launch
                }

                _state.update {
                    it.copy(
                        mediaUrl = imageUrl,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error uploading image", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to upload image: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun uploadToCloudinary(file: File): String? = withContext(Dispatchers.IO) {
        val cloudName = "dbeximude"
        val uploadPreset = "Kotlin"

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        return@withContext try {
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                val url = json.getString("secure_url")
                Log.d("Cloudinary", "✅ Uploaded to: $url")
                url
            } else {
                Log.e("Cloudinary", "Upload failed: ${response.code} ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.e("Cloudinary", "Upload exception", e)
            null
        }
    }

    private fun copyUriToTempFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("ad_upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("Cloudinary", "Failed to copy URI to temp file", e)
            null
        }
    }
}