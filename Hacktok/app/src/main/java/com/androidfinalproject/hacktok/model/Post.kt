package com.androidfinalproject.hacktok.model

import androidx.lifecycle.viewmodel.CreationExtras
import org.bson.types.ObjectId
import java.util.Date

data class Post(
    val id: ObjectId? = null,
    val content: String,
    val user: User,  // Tham chiếu đến người dùng đã tạo post
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    var likes: Int = 0,
    var comments: MutableList<Any> = mutableListOf()
) {
    override fun toString(): String {
        return "id: $id, content: $content, created by: ${user.username}\n"
    }
    private fun addCmt(cmt:String){
        comments.add(cmt)
    }
}
