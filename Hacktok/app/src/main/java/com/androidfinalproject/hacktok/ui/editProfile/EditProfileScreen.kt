package com.androidfinalproject.hacktok.ui.editProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.UserRole
import com.androidfinalproject.hacktok.ui.messageDashboard.component.ProfileImage
import com.androidfinalproject.hacktok.ui.editProfile.component.CustomTextField
import com.androidfinalproject.hacktok.ui.editProfile.component.DropdownField
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = viewModel()
) {
    val username by viewModel.username.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val role by viewModel.role.collectAsState()
    val errorState by viewModel.errorState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Edit profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            ProfileImage(imageSize = 40.dp, modifier = Modifier.fillMaxWidth(), contentDescription = "Profile image", isActive = false)
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Username",
            value = username,
            isError = errorState["username"] ?: false,
            onValueChange = { viewModel.updateField("username", it) }
        )

        CustomTextField(
            label = "Full Name",
            value = fullName,
            isError = errorState["fullName"] ?: false,
            onValueChange = { viewModel.updateField("fullName", it) }
        )

        CustomTextField(
            label = "Email",
            value = email,
            isError = errorState["email"] ?: false,
            onValueChange = { viewModel.updateField("email", it) }
        )

        CustomTextField(
            label = "Bio",
            value = bio,
            isError = errorState["bio"] ?: false,
            onValueChange = { viewModel.updateField("bio", it) }
        )

        DropdownField(
            label = "Role",
            selectedValue = role.name,
            options = UserRole.entries.map { it.name },
            onValueChange = { viewModel.updateField("role", it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { /* Handle Cancel */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel", color = Color.Black)
            }

            Button(
                onClick = {
                    viewModel.saveProfile()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)), // Orange
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }
}