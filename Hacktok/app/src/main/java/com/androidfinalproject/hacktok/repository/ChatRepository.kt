package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getOrCreateChat(currentUserId: String, otherUserId: String): String
    fun getChatMessagesFlow(chatId: String): Flow<List<Message>>
    fun getChatFlow(chatId: String): Flow<Chat?>
    fun getUserChatsFlow(userId: String): Flow<List<Chat>>
    suspend fun sendMessage(chatId: String, message: Message): String
    suspend fun deleteMessage(chatId: String, messageId: String)
    suspend fun deleteChat(chatId: String)
}