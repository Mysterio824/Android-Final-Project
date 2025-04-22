package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.repository.ChatRepository
import com.androidfinalproject.hacktok.service.FcmService
import com.androidfinalproject.hacktok.service.RelationshipService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val fcmService: FcmService,
    private val relationshipService: RelationshipService
) : ChatRepository {
    private val chatsCollection = db.collection("chats")
    private val TAG = "ChatRepository"
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private fun generateChatId(userA: String, userB: String): String {
        return listOf(userA, userB).sorted().joinToString("_")
    }

    // Create or get existing chat between two users
    override suspend fun getOrCreateChat(currentUserId: String, otherUserId: String): String {
        val chatId = generateChatId(currentUserId, otherUserId)

        val existingChatSnapshot = chatsCollection.document(chatId).get().await()
        if (existingChatSnapshot.exists()) {
            return chatId
        }

        val newChat = Chat(
            id = chatId,
            participants = listOf(currentUserId, otherUserId),
            lastMessage = "",
        )

        // Try to create document with that ID
        chatsCollection.document(chatId).set(newChat).await()
        return chatId
    }

    // Get real-time chat messages
    override fun getChatMessagesFlow(chatId: String): Flow<List<Message>> = callbackFlow {
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
    override fun getChatFlow(chatId: String): Flow<Chat?> = callbackFlow {
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
    override fun getUserChatsFlow(userId: String): Flow<List<Chat>> = callbackFlow {
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
    override suspend fun sendMessage(chatId: String, message: Message): String {
        try {
            // Get information about the chat
            val chat = chatsCollection.document(chatId).get().await().toObject<Chat>()
                ?: throw Exception("Chat not found")
            val recipientUserId = chat.participants.find { it != message.senderId }
                ?: throw Exception("User not found")

                // Check relationship status before sending notification
            val relationshipStatus = relationshipService.getRelationshipStatus(recipientUserId)

                // Only send notification if user is not blocked
            if (relationshipStatus == RelationshipStatus.BLOCKED ||
                    relationshipStatus == RelationshipStatus.BLOCKING) {
                return ""
            }
            val messageRef = chatsCollection.document(chatId)
                .collection("messages")
                .add(message)
                .await()

            Log.d(TAG, "Message sent: ${message.content}")
            val userIndex = if (chat.participants[0] == recipientUserId) "unreadCountUser1" else "unreadCountUser2"
            val currentUnreadCount = if (chat.participants[0] == recipientUserId)
                chat.unreadCountUser1 else chat.unreadCountUser2

            // Update chat's last message details
            chatsCollection.document(chatId).update(
                mapOf(
                    "lastMessage" to message.content,
                    "lastMessageAt" to message.createdAt,
                    userIndex to currentUnreadCount + 1
                ),
            ).await()

            val isRecipientUser1 = chat.participants[0] == recipientUserId
            val isMuted = if (isRecipientUser1) chat.isMutedByUser1 else chat.isMutedByUser2

            // Only send notification if chat is not muted by recipient
            if (!isMuted) {
                serviceScope.launch {
                    fcmService.sendInteractionNotification(
                        recipientUserId = recipientUserId,
                        senderUserId = message.senderId,
                        notificationType = NotificationType.NEW_MESSAGE,
                        itemId = chatId,
                    )
                }
            }
            return messageRef.id
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            throw e
        }
    }

    // Delete a message
    override suspend fun deleteMessage(chatId: String, messageId: String) {
        chatsCollection.document(chatId)
            .collection("messages")
            .document(messageId)
            .update("isDeleted", true)
            .await()
    }

    // Delete a chat
    override suspend fun deleteChat(chatId: String) {

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
    override suspend fun getUserChats(userId: String): List<Chat> {
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


    override suspend fun setMuteState(chatId: String, currentUserId: String, setMute: Boolean): Boolean {
        try {
            val chatDoc = chatsCollection.document(chatId).get().await()
            val chat = chatDoc.toObject<Chat>() ?: throw Exception("Chat not found")

            // Determine which user is setting the mute state
            val isUser1 = chat.participants[0] == currentUserId
            val muteField = if (isUser1) "isMutedByUser1" else "isMutedByUser2"

            // Update the mute state
            chatsCollection.document(chatId).update(muteField, setMute).await()

            Log.d(TAG, "Chat mute state updated: chatId=$chatId, user=$currentUserId, muted=$setMute")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting mute state for chat: $chatId", e)
            return false
        }
    }
}