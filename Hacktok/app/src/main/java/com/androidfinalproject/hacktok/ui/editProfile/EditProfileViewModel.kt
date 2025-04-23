package com.androidfinalproject.hacktok.ui.editProfile

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    _state.update {
                        it.copy(
                            username = user.username ?: "",
                            fullName = user.fullName ?: "Unknown",
                            email = user.email,
                            bio = user.bio ?: "",
                            avatarUrl = user.profileImage ?: "",
                            errorState = emptyMap(),
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = "Failed to load user profile"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.UpdateField -> {
                when (action.field) {
                    "username" -> _state.update { it.copy(username = action.value) }
                    "fullName" -> _state.update { it.copy(fullName = action.value) }
                    "email" -> _state.update { it.copy(email = action.value) }
                    "bio" -> _state.update { it.copy(bio = action.value) }
                }
            }
            is EditProfileAction.UpdateAvatar -> {
                _state.update { it.copy(avatarUri = action.uri) }
            }
            EditProfileAction.SaveProfile -> {
                if (validateFields()) {
                    saveProfile()
                }
            }
            EditProfileAction.Cancel -> {
                loadCurrentUser()
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser != null) {
                    val avatarUrl = if (_state.value.avatarUri != null) {
                        val tempFile = copyUriToTempFile(getApplication<Application>().applicationContext, _state.value.avatarUri!!)
                        if (tempFile == null || !tempFile.exists()) {
                            Log.e("Upload", "Temp file is null or doesn't exist.")
                            _state.value.avatarUrl
                        } else {
                            uploadToCloudinary(tempFile).also {
                                tempFile.delete()
                            } ?: _state.value.avatarUrl
                        }
                    } else {
                        _state.value.avatarUrl
                    }

                    val updatedUser = currentUser.copy(
                        username = _state.value.username,
                        fullName = _state.value.fullName,
                        email = _state.value.email,
                        bio = _state.value.bio,
                        profileImage = avatarUrl
                    )
                    
                    val success = userRepository.updateUserProfile(updatedUser)
                    
                    if (success) {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null,
                                avatarUrl = avatarUrl
                            )
                        }
                    } else {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = "Failed to update profile"
                            )
                        }
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = "User not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Error: ${e.message}"
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
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("Cloudinary", "Failed to copy URI to temp file", e)
            null
        }
    }

    private fun validateFields(): Boolean {
        val errors = mutableMapOf<String, Boolean>()
        errors["username"] = _state.value.username.isBlank()
        errors["email"] = _state.value.email.isBlank()

        _state.update { it.copy(errorState = errors) }
        return !errors.containsValue(true)
    }
}