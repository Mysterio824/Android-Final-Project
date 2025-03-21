package com.androidfinalproject.hacktok.ui.mainDashboard;

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.Post

@Composable
fun PostItem(post: Post) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.user.username, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.content)
            Spacer(modifier = Modifier.height(8.dp))

            // Danh sách ảnh có sẵn trong `res/drawable`
            val sampleImages = listOf(
                R.drawable.sample,
                R.drawable.sample,
                R.drawable.sample
            )

            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(sampleImages) { imageResId ->
                    ImageItem(imageResId)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            PostActionBar()
        }
    }
}

@Composable
fun ImageItem(imageResId: Int) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "Post Image",
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
    )
}


