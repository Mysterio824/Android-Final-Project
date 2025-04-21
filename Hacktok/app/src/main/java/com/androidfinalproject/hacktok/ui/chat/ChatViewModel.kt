package com.androidfinalproject.hacktok.ui.chat

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Media
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.AuthRepository
import com.androidfinalproject.hacktok.repository.ChatRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.RelationshipService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val relationshipService: RelationshipService,
    application: Application
) : AndroidViewModel(application) {
    private var otherUserId: String? = null
    
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    fun setUserId(userId: String) {
        otherUserId = userId
        loadChat()
    }

    private fun loadChat() {
        val userId = otherUserId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val firebaseUser = authRepository.getCurrentUser()
                if (firebaseUser == null) {
                    _state.update { it.copy(error = "Not authenticated", isLoading = false) }
                    return@launch
                }

                // Get or create chat
                val chatId = chatRepository.getOrCreateChat(firebaseUser.uid, userId)
                
                // Load other user's data
                val otherUser = userRepository.getUserById(userId)
                        ?: run{
                            _state.update { it.copy(error = "User not found", isLoading = false) }
                            return@launch
                        }

                // Convert FirebaseUser to our User model using the companion object method
                val currentUser = User.fromFirebaseUser(firebaseUser)

                val relationInfo = relationshipService.getRelationship(otherUser.id!!)

                // Update state with user info
                _state.update {
                    it.copy(
                        chatId = chatId,
                        currentUser = currentUser,
                        relation = relationInfo,
                        otherUser = otherUser,
                    )
                }

                _state.update { it.copy(isLoading = false) }

                // Load messages
                chatRepository.getChatMessagesFlow(chatId).collect { messages ->
                    _state.update { it.copy(messages = messages) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "An error occurred",
                    isLoading = false
                ) }
            }
        }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.SendMessage -> sendMessage(action.message)
            is ChatAction.SendImage -> sendImage(action.imageUri)
            is ChatAction.DeleteMessage -> deleteMessage(action.messageId)
            is ChatAction.LoadInitialMessages -> loadChat()
            is ChatAction.ToggleMute -> toggleMute()
            is ChatAction.CreateGroup -> createGroup()
            is ChatAction.FindInChat -> findInChat()
            is ChatAction.DeleteChat -> deleteChat()
            is ChatAction.BlockUser -> blockUser()
            is ChatAction.NavigateToManageUser -> {} // Handled by navigation
            else -> {}
        }
    }

    private fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: return@launch
                val chatId = chatRepository.getOrCreateChat(currentUser.uid, otherUserId!!)

                val message = Message(
                    senderId = currentUser.uid,
                    content = content,
                    createdAt = Date()
                )

                chatRepository.sendMessage(chatId, message)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to send message: ${e.message}") }
            }
        }
    }

    private fun sendImage(imageUri: String) {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: return@launch
                val chatId = chatRepository.getOrCreateChat(currentUser.uid, otherUserId!!)

                // Upload image to Cloudinary
                val imageUrl = uploadToCloudinary(imageUri) ?: run {
                    _state.update { it.copy(error = "Failed to upload image") }
                    return@launch
                }

                val message = Message(
                    senderId = currentUser.uid,
                    content = "",
                    createdAt = Date(),
                    media = Media(
                        type = "image",
                        url = imageUrl
                    )
                )

                chatRepository.sendMessage(chatId, message)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to send image: ${e.message}") }
            }
        }
    }

    private suspend fun uploadToCloudinary(imageUri: String): String? = withContext(Dispatchers.IO) {
        try {
            val context = getApplication<Application>().applicationContext
            val uri = Uri.parse(imageUri)
            val tempFile = copyUriToTempFile(context, uri) ?: return@withContext null

            val cloudName = "dbeximude"
            val uploadPreset = "Kotlin"

            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", tempFile.name, tempFile.asRequestBody("image/*".toMediaTypeOrNull()))
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            tempFile.delete()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
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
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
            tempFile.outputStream().use { output ->
                inputStream?.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error creating temp file", e)
            null
        }
    }

    private fun deleteMessage(messageId: String?) {
        if (messageId == null) return

        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: return@launch
                val chatId = chatRepository.getOrCreateChat(currentUser.uid, otherUserId!!)
                chatRepository.deleteMessage(chatId, messageId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete message: ${e.message}") }
            }
        }
    }

    private fun deleteChat() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: return@launch
                val chatId = chatRepository.getOrCreateChat(currentUser.uid, otherUserId!!)
                chatRepository.deleteChat(chatId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete chat: ${e.message}") }
            }
        }
    }

    private fun toggleMute() {
        _state.update { it.copy(isUserMuted = !it.isUserMuted) }
        // TODO: Implement mute functionality in repository
    }

    private fun createGroup() {
        // TODO: Implement group creation
    }

    private fun findInChat() {
        // TODO: Implement chat search
    }

    private fun blockUser() {
        // TODO: Implement block user functionality
    }
}

