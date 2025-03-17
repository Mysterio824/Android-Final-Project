package com.hacktok.androidfinalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hacktok.androidfinalproject.model.User
import com.hacktok.androidfinalproject.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bson.Document
import org.bson.types.ObjectId

class UserViewModel : ViewModel() {

    // StateFlow for users list
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    // StateFlow for error messages
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Loading state indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchUsers()
    }

    // Fetch all users from the repository and update StateFlow
    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userList = UsersRepository.listUsers()
                _users.value = userList
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load users: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add a new user, then refresh the list
    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                UsersRepository.insertUser(user)
                fetchUsers()
            } catch (e: Exception) {
                _error.value = "Failed to add user: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update an existing user by its ObjectId, then refresh the list
    fun updateUser(id: ObjectId, update: Document) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                UsersRepository.updateUser(id, update)
                fetchUsers()
            } catch (e: Exception) {
                _error.value = "Failed to update user: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete a user by its ObjectId, then refresh the list
    fun deleteUser(id: ObjectId) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                UsersRepository.deleteUser(id)
                fetchUsers()
            } catch (e: Exception) {
                _error.value = "Failed to delete user: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // (Optional) Function to get a specific user by ID
    fun getUserById(id: ObjectId) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // You might update another StateFlow to hold the individual user.
                val user = UsersRepository.findUserById(id)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to get user: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
