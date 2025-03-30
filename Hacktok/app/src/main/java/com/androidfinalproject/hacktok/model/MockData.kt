package com.androidfinalproject.hacktok.model

import org.bson.types.ObjectId

object MockData {
    val mockUsers = listOf(
        User("", "john", "john@example.com"),
        User("", "jane_smith", "jane@example.com"),
        User("", "bob_jones", "bob@example.com")
    )

    val mockPosts = listOf(
        Post("", "Hello world!", "2", likeCount = 10),
        Post("", "Learning Kotlin!", "2", likeCount = 1000),
        Post("", "Jetpack Compose is fun!", "2", likeCount = 123)
    )

    val mockComments = listOf(
        Comment("", "Hello world", "10", "1"),
        Comment("", "Hello world", "10", "1"),
        Comment("", "Hello world", "10", "1"),
        Comment("", "Hello world", "10", "1"),
        Comment("", "Hello world", "10", "1"),
        Comment("", "Great post! I really enjoyed reading this.","5", "2"),
        Comment("", "I have a question about this. Can you elaborate more?", "2", "2"),
        Comment("", "This is exactly what I needed to know. Thanks for sharing!", "8", "2")
    )
}


