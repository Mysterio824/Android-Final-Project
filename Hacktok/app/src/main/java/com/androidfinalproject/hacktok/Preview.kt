package com.androidfinalproject.hacktok

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.auth.LoginScreen
import com.androidfinalproject.hacktok.ui.auth.LoginState
import com.androidfinalproject.hacktok.ui.friendList.FriendListScreen
import com.androidfinalproject.hacktok.ui.friendList.FriendListState
import com.androidfinalproject.hacktok.ui.theme.LoginAppTheme
import org.bson.types.ObjectId

@Preview(showBackground = true)
@Composable
fun FriendListScreenPreview() {
    LoginAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            val previewState = FriendListState(
                users = listOf(
                    User(
                        username = "john_doe",
                        email = "john@example.com"
                    ),
                    User(
                        username = "jane_smith",
                        email = "jane@example.com"
                    ),
                    User(
                        username = "robert_johnson",
                        email = "robert@example.com"
                    ),
                    User(
                        username = "robert_johnson",
                        email = "robert@example.com"
                    )
                ),
                filteredUsers = listOf(
                    User(
                        username = "john_doe",
                        email = "john@example.com"
                    ),
                    User(
                        username = "jane_smith",
                        email = "jane@example.com"
                    ),
                    User(
                        username = "robert_johnson",
                        email = "robert@example.com"
                    ),
                    User(
                        username = "robert_johnson",
                        email = "robert@example.com"
                    )
                ),
                friendIds = setOf(ObjectId())
            )

            FriendListScreen(
                state = previewState,
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            LoginScreen (
                state = LoginState(
                    isLoginMode = false
                ),
                onAction = {},
            )
        }
    }
}