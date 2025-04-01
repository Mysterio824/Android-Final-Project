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
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.UserRole
import com.androidfinalproject.hacktok.ui.messageDashboard.component.ProfileImage
import com.androidfinalproject.hacktok.ui.editProfile.component.CustomTextField
import com.androidfinalproject.hacktok.ui.editProfile.component.DropdownField

@Composable
fun EditProfileScreen(
    user: User
) {
    var username by remember { mutableStateOf(user.username) }
    var fullName by remember { mutableStateOf(user.fullName ?: "Unknown") }
    var email by remember { mutableStateOf(user.email) }
    var bio by remember { mutableStateOf(user.bio ?: "") }
    var role by remember { mutableStateOf(user.role.name) }

    var isUsernameError by remember { mutableStateOf(false) }
    var isFullNameError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isBioError by remember { mutableStateOf(false) }

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

        CustomTextField(label = "Username", value = username, isError = isUsernameError, onValueChange = { username = it })
        CustomTextField(label = "Full Name", value = fullName, isError = isFullNameError, onValueChange =  { fullName = it })
        CustomTextField(label = "Email", value = email, isError = isEmailError, onValueChange = { email = it })
        CustomTextField(label = "Bio", value = bio, isError = isBioError, onValueChange =  { bio = it })
        DropdownField(label = "Role", selectedValue = role, options = UserRole.entries.map { it.name }, onValueChange = { role = it })

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
                    isUsernameError = username.isBlank()
                    isFullNameError = fullName.isBlank()
                    isEmailError = email.isBlank()
                    isBioError = bio.isBlank()

                    if (!(isUsernameError || isFullNameError || isEmailError || isBioError)) {
                        val updatedUser = user.copy(
                            username = username,
                            fullName = fullName,
                            email = email,
                            bio = bio,
                            role = UserRole.valueOf(role)
                        )
                        // Save updatedUser to backend or state management
                    }
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