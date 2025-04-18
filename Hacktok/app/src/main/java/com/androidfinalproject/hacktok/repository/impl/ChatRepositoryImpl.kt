package com.androidfinalproject.hacktok.repository.impl

import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.repository.ChatRepository
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
class ChatRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ChatRepository {

    private val chatsCollection = db.collection("chats")

    override suspend fun getOrCreateChat(currentUserId: String, otherUserId: String): String {
        val existingChat = chatsCollection
            .whereArrayContains("participants", currentUserId)
            .get()
            .await()
            .documents
            .firstOrNull { doc ->
                val chat = doc.toObject<Chat>()
                chat?.participants?.containsAll(listOf(currentUserId, otherUserId)) == true
            }

        if (existingChat != null) return existingChat.id

        val newChat = Chat(
            participants = listOf(currentUserId, otherUserId),
            lastMessage = "",
            lastMessageAt = java.util.Date()
        )

        val chatRef = chatsCollection.add(newChat).await()
        chatsCollection.document(chatRef.id).update("id", chatRef.id).await()
        return chatRef.id
    }

    override fun getChatMessagesFlow(chatId: String): Flow<List<Message>> = callbackFlow {
        val messagesRef = chatsCollection.document(chatId).collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)

        val subscription = messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val messages = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<Message>()?.copy(id = doc.id)
            } ?: emptyList()
            trySend(messages)
        }

        awaitClose { subscription.remove() }
    }

    override fun getChatFlow(chatId: String): Flow<Chat?> = callbackFlow {
        val subscription = chatsCollection.document(chatId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val chat = snapshot?.toObject<Chat>()
                trySend(chat)
            }

        awaitClose { subscription.remove() }
    }

    override fun getUserChatsFlow(userId: String): Flow<List<Chat>> = callbackFlow {
        val subscription = chatsCollection
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val chats = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<Chat>()
                } ?: emptyList()
                trySend(chats)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(chatId: String, message: Message): String {
        val messageRef = chatsCollection.document(chatId)
            .collection("messages")
            .add(message)
            .await()

        chatsCollection.document(chatId).update(
            mapOf(
                "lastMessage" to message.content,
                "lastMessageAt" to message.createdAt
            )
        ).await()

        return messageRef.id
    }

    override suspend fun deleteMessage(chatId: String, messageId: String) {
        chatsCollection.document(chatId)
            .collection("messages")
            .document(messageId)
            .update("isDeleted", true)
            .await()
    }

    override suspend fun deleteChat(chatId: String) {
        val messages = chatsCollection.document(chatId)
            .collection("messages")
            .get()
            .await()

        for (message in messages) {
            message.reference.delete().await()
        }

        chatsCollection.document(chatId).delete().await()
    }
}
