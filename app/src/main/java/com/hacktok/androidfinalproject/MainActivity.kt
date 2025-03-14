package com.hacktok.androidfinalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.hacktok.androidfinalproject.viewmodel.UserViewModel
class MainActivity : ComponentActivity() {

    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
            }
        }
    }
}

