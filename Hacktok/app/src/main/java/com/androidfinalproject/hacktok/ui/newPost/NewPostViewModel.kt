package com.androidfinalproject.hacktok.ui.newPost

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class NewPostViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    application: Application,
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(NewPostState())
    private var currentUser: User? = null
    val state: StateFlow<NewPostState> = _state

    init {
        viewModelScope.launch {
            currentUser = userRepository.getCurrentUser()
            _state.value = _state.value.copy(username = currentUser?.fullName ?: "Unknown", imageLink = currentUser?.profileImage ?: "")
        }
    }

    fun onAction(action: NewPostAction) {
        when (action) {
            is NewPostAction.UpdatePrivacy -> _state.value = _state.value.copy(privacy = action.privacy)
            is NewPostAction.UpdateCaption -> _state.value = _state.value.copy(caption = action.caption)
            is NewPostAction.RemoveImage -> _state.value = _state.value.copy(imageUri = null)
            is NewPostAction.SubmitPost -> submitPost()
            is NewPostAction.UpdateImageUri -> _state.value = _state.value.copy(imageUri = action.uri)
            is NewPostAction.ClearSubmissionState -> _state.value = _state.value.copy(postSubmitted = false)
            is NewPostAction.LoadPostForEditing -> {
                viewModelScope.launch {
                    try {
                        val post = postRepository.getPost(action.postId)
                        post?.let {
                            _state.value = _state.value.copy(
                                postId = it.id,
                                caption = it.content,
                                imageUri = Uri.parse(it.imageLink),
                                privacy = PRIVACY.valueOf(it.privacy.uppercase()),
                                isEditing = true
                            )
                        } ?: Log.e("EditPost", "Post not found for ID: ${action.postId}")
                    } catch (e: Exception) {
                        Log.e("EditPost", "Failed to load post for editing", e)
                    }
                }
            }
            else -> {

            }
        }
    }

    private fun submitPost() {
        val user = auth.currentUser ?: run {
            Log.e("NewPost", "No authenticated user.")
            return
        }

        val context = getApplication<Application>().applicationContext
        val userId = user.uid
        val caption = state.value.caption
        val imageUri = state.value.imageUri
        val privacy = state.value.privacy.name

        Log.d("NewPost", "Starting post submission...")

        viewModelScope.launch {
            try {
                val imageLink = if (imageUri != null && imageUri.toString().startsWith("content://")) {
                    val tempFile = copyUriToTempFile(context, imageUri)
                    if (tempFile == null || !tempFile.exists()) {
                        Log.e("Upload", "Temp file is null or doesn't exist.")
                        ""
                    } else {
                        uploadToCloudinary(tempFile).also {
                            tempFile.delete()
                        } ?: ""
                    }
                } else {
                    imageUri?.toString() ?: ""
                }

                if (state.value.isEditing) {
                    val postId = state.value.postId
                    if (!postId.isNullOrBlank()) {
                        postRepository.updatePostContentOnly(
                            postId = postId,
                            newContent = caption,
                            newPrivacy = privacy,
                            newImageLink = imageLink
                        )
                        _state.value = _state.value.copy(postSubmitted = true)
                        Log.d("Post", "Post updated: $postId")
                    } else {
                        Log.e("Post", "Editing mode but no post ID provided.")
                    }
                } else {
                    val post = Post(
                        content = caption,
                        userId = userId,
                        imageLink = imageLink,
                        privacy = privacy,
                    )
                    val postId = postRepository.addPost(post)
                    _state.value = _state.value.copy(postSubmitted = true)
                    Log.d("Post", "Post created with ID: $postId")
                }
            } catch (e: Exception) {
                Log.e("Post", "Failed to submit post", e)
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
}