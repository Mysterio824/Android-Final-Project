package com.androidfinalproject.hacktok.repository

import android.util.Log
import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val chatsCollection = db.collection("chats")
    private val TAG = "ChatRepository"
    
    // Create or get existing chat between two users
    suspend fun getOrCreateChat(currentUserId: String, otherUserId: String): String {
        // First try to find existing chat
        val existingChat = chatsCollection
            .whereArrayContains("participants", currentUserId)
            .get()
            .await()
            .documents
            .firstOrNull { doc ->
                val chat = doc.toObject<Chat>()
                chat?.participants?.containsAll(listOf(currentUserId, otherUserId)) == true
            }

        if (existingChat != null) {
            return existingChat.id
        }

        // Create new chat if none exists
        val newChat = Chat(
            participants = listOf(currentUserId, otherUserId),
            lastMessage = "",
            lastMessageAt = java.util.Date()
        )
        
        val chatRef = chatsCollection.add(newChat).await()
        chatsCollection.document(chatRef.id).update("id", chatRef.id).await()
        return chatRef.id
    }

    // Get real-time chat messages
    fun getChatMessagesFlow(chatId: String): Flow<List<Message>> = callbackFlow {
        val messagesRef = chatsCollection.document(chatId).collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)

        val subscription = messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }
            
            val messages = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<Message>()?.copy(id = doc.id)
            } ?: emptyList()
            
            trySend(messages)
        }

        awaitClose { subscription.remove() }
    }

    // Get real-time chat updates
    fun getChatFlow(chatId: String): Flow<Chat?> = callbackFlow {
        val subscription = chatsCollection.document(chatId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val chat = snapshot?.toObject<Chat>()
                trySend(chat)
            }

        awaitClose { subscription.remove() }
    }

    // Get user's chats
    fun getUserChatsFlow(userId: String): Flow<List<Chat>> = callbackFlow {
        Log.d(TAG, "Attempting getUserChatsFlow query for userId: $userId")
        val query = chatsCollection
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageAt", Query.Direction.DESCENDING)

        Log.d(TAG, "getUserChatsFlow Firestore query constructed for userId: $userId")
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to user chats flow for userId: $userId", error)
                // Handle error
                close(error)
                return@addSnapshotListener
            }
            val chats = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<Chat>()
            } ?: emptyList()
            Log.d(TAG, "getUserChatsFlow snapshot received for userId: $userId, chats count: ${chats.size}")
            trySend(chats)
        }

        awaitClose { 
            Log.d(TAG, "Closing user chats flow listener for userId: $userId")
            subscription.remove() 
        }
    }

    // Send a message
    suspend fun sendMessage(chatId: String, message: Message): String {
        val messageRef = chatsCollection.document(chatId)
            .collection("messages")
            .add(message)
            .await()

        // Update chat's last message
        chatsCollection.document(chatId).update(
            mapOf(
                "lastMessage" to message.content,
                "lastMessageAt" to message.createdAt
            )
        ).await()

        return messageRef.id
    }

    // Delete a message
    suspend fun deleteMessage(chatId: String, messageId: String) {
        chatsCollection.document(chatId)
            .collection("messages")
            .document(messageId)
            .update("isDeleted", true)
            .await()
    }

    // Delete a chat
    suspend fun deleteChat(chatId: String) {
        // First delete all messages
        val messages = chatsCollection.document(chatId)
            .collection("messages")
            .get()
            .await()

        for (message in messages) {
            message.reference.delete().await()
        }

        // Then delete the chat document
        chatsCollection.document(chatId).delete().await()
    }

    // Get user's chats
    suspend fun getUserChats(userId: String): List<Chat> {
        Log.d(TAG, "Attempting getUserChats query for userId: $userId (NO ORDERING - DIAGNOSTIC)")
        // Temporarily removed orderBy for diagnostics
        val query = chatsCollection
            .whereArrayContains("participants", userId)
            // .orderBy("lastMessageAt", Query.Direction.DESCENDING) // <--- Temporarily commented out
        
        Log.d(TAG, "getUserChats Firestore query constructed for userId: $userId (NO ORDERING - DIAGNOSTIC)")
        return try {
             val snapshot = query.get().await()
             Log.d(TAG, "getUserChats snapshot received for userId: $userId, documents count: ${snapshot.size()} (NO ORDERING - DIAGNOSTIC)")
             // Client-side sorting would be needed here if this was permanent
             snapshot.documents.mapNotNull { doc ->
                doc.toObject(Chat::class.java)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user chats for userId: $userId (NO ORDERING - DIAGNOSTIC)", e)
            emptyList()
        }
    }
}