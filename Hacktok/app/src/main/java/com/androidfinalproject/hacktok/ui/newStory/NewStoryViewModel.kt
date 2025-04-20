package com.androidfinalproject.hacktok.ui.newStory

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Media
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.service.StoryService
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class NewStoryViewModel @Inject constructor(
    application: Application,
    private val storyService: StoryService
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(NewStoryState())
    val state: StateFlow<NewStoryState> = _state

    fun onAction(action: NewStoryAction) {
        when (action) {
            is NewStoryAction.GoToImageEditor -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedImageUri = action.imageUri,
                        storyType = "image"
                    )
                }
            }
            is NewStoryAction.NewTextStory -> {
                _state.update { currentState ->
                    currentState.copy(
                        storyType = "text"
                    )
                }
            }
            is NewStoryAction.UpdatePrivacy -> {
                _state.update { currentState ->
                    currentState.copy(privacySetting = action.privacy)
                }
            }
            is NewStoryAction.UpdateText -> {
                _state.update { currentState ->
                    currentState.copy(storyText = action.text)
                }
            }
            is NewStoryAction.CreateImageStory -> {
                createImageStory(action.imageUri, action.privacy)
            }
            is NewStoryAction.CreateTextStory -> {
                createTextStory(action.text, action.privacy)
            }
            is NewStoryAction.ResetState -> {
                resetState()
            }
            else -> {}
        }
    }

    private fun createImageStory(imageUri: Uri?, privacy: PRIVACY) {
        if (imageUri == null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val context = getApplication<Application>().applicationContext
                val tempFile = copyUriToTempFile(context, imageUri)

                if (tempFile == null || !tempFile.exists()) {
                    _state.update {
                        it.copy(isLoading = false, error = "Không thể đọc tệp hình ảnh")
                    }
                    return@launch
                }

                val imageUrl = uploadToCloudinary(tempFile)
                tempFile.delete()

                if (imageUrl.isNullOrBlank()) {
                    _state.update {
                        it.copy(isLoading = false, error = "Tải ảnh lên thất bại")
                    }
                    return@launch
                }

                val media = Media(
                    type = "image",
                    url = imageUrl,
                    thumbnailUrl = imageUrl // Có thể sinh thumbnail riêng nếu muốn
                )

                val result = storyService.createStory(media, privacy)

                if (result.isSuccess) {
                    _state.update {
                        it.copy(isLoading = false, isStoryCreated = true, error = null)
                    }
                } else {
                    _state.update {
                        it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                    }
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
    private fun createTextStory(text: String, privacy: PRIVACY) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val media = Media(
                    type = "text",
                    url = text, // For text stories, store text content in url field
                    thumbnailUrl = "" // No thumbnail for text
                )

                val result = storyService.createStory(
                    media = media,
                    privacy = privacy
                )

                if (result.isSuccess) {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isStoryCreated = true,
                            error = null
                        )
                    }
                } else {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = result.exceptionOrNull()?.message ?: "Failed to create story"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create story"
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
            val tempFile = File.createTempFile("story_upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("Cloudinary", "Failed to copy URI to temp file", e)
            null
        }
    }

    fun resetState() {
        _state.update { NewStoryState() }
    }
}