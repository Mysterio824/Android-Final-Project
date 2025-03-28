package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Group
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GroupRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("groups")

    // Thêm nhóm mới
    suspend fun addGroup(group: Group): String {
        val documentRef = collection.add(group).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy nhóm theo ID
    suspend fun getGroup(groupId: String): Group? {
        val snapshot = collection.document(groupId).get().await()
        return snapshot.toObject(Group::class.java)
    }

    // Thêm thành viên vào nhóm
    suspend fun addMember(groupId: String, userId: String) {
        collection.document(groupId)
            .update("members", FieldValue.arrayUnion(userId)).await()
    }

    // Xóa thành viên khỏi nhóm
    suspend fun removeMember(groupId: String, userId: String) {
        collection.document(groupId)
            .update("members", FieldValue.arrayRemove(userId)).await()
    }

    // Cập nhật nhóm
    suspend fun updateGroup(groupId: String, updates: Map<String, Any>) {
        collection.document(groupId).update(updates).await()
    }

    // Xóa nhóm
    suspend fun deleteGroup(groupId: String) {
        collection.document(groupId).delete().await()
    }
}