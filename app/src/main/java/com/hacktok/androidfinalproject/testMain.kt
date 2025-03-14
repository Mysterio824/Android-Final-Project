package com.hacktok.androidfinalproject

import com.hacktok.androidfinalproject.model.User
import com.hacktok.androidfinalproject.repository.MongoDbManager
import com.hacktok.androidfinalproject.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

suspend fun main() {
    try {
        val _users = MutableStateFlow<List<User>>(emptyList())
        _users.value = UsersRepository.listUsers()
        println(_users.first().toString())
        MongoDbManager.close()
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }
}