package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("chats")

    // Thêm chat mới
    suspend fun addChat(chat: Chat): String {
        val documentRef = collection.add(chat).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy chat theo ID
    suspend fun getChat(chatId: String): Chat? {
        val snapshot = collection.document(chatId).get().await()
        return snapshot.toObject(Chat::class.java)
    }

    // Lấy danh sách chat của một người dùng
    suspend fun getChatsByUser(userId: String): List<Chat> {
        val snapshot = collection.whereArrayContains("participants", userId).get().await()
        return snapshot.toObjects(Chat::class.java)
    }

    // Cập nhật chat (ví dụ: lastMessage)
    suspend fun updateChat(chatId: String, updates: Map<String, Any>) {
        collection.document(chatId).update(updates).await()
    }

    // Xóa chat
    suspend fun deleteChat(chatId: String) {
        collection.document(chatId).delete().await()
    }

    // Thêm tin nhắn vào subcollection messages
    suspend fun addMessage(chatId: String, message: Message): String {
        val documentRef = collection.document(chatId).collection("messages").add(message).await()

        // Updated to remove the undefined type 'T'
        collection.document(chatId).update(
            mapOf(
                "lastMessage" to message.content,
                "lastMessageAt" to message.createdAt
            )
        ).await()

        return documentRef.id
    }

    // Lấy danh sách tin nhắn của một chat
    suspend fun getMessages(chatId: String): List<Message> {
        val snapshot = collection.document(chatId).collection("messages").get().await()
        return snapshot.toObjects(Message::class.java)
    }
}