package com.androidfinalproject.hacktok.model

import org.bson.types.ObjectId

object MockData {
    val mockUsers = listOf(
        User(ObjectId(), "john", "john@example.com"),
        User(ObjectId(), "jane_smith", "jane@example.com"),
        User(ObjectId(), "bob_jones", "bob@example.com")
    )

    val mockPosts = listOf(
        Post(ObjectId(), "Hello world!", mockUsers[0], likeCount = 10),
        Post(ObjectId(), "Learning Kotlin!", mockUsers[1], likeCount = 1000),
        Post(ObjectId(), "Jetpack Compose is fun!", mockUsers[2], likeCount = 123)
    )

    val mockComments = listOf(
        Comment(ObjectId(), "Hello world", 10, mockUsers[0]),
        Comment(ObjectId(), "Hello world", 10, mockUsers[0]),
        Comment(ObjectId(), "Hello world", 10, mockUsers[0]),
        Comment(ObjectId(), "Hello world", 10, mockUsers[0]),
        Comment(ObjectId(), "Hello world", 10, mockUsers[0]),
        Comment(ObjectId(), "Great post! I really enjoyed reading this.",5, User(username = "user1", email = "user1@example.com")),
        Comment(ObjectId(), "I have a question about this. Can you elaborate more?", 2, User(username = "user2", email = "user2@example.com")),
        Comment(ObjectId(), "This is exactly what I needed to know. Thanks for sharing!", 8, User(username = "user3", email = "user3@example.com"))
    )
}


