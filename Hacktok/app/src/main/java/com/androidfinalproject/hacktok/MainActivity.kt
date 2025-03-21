package com.androidfinalproject.hacktok
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainAppTheme {
                App()
            }
        }
    }
}
