const express = require('express');
const admin = require('firebase-admin');
const axios = require('axios'); // You'll need to install this package
const app = express();

// Better to use environment variables for configuration
const serviceAccount = process.env.FIREBASE_SERVICE_ACCOUNT 
  ? JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT) 
  : require("./android-91bea-firebase-adminsdk-fbsvc-926d39bbea.json");

const FIREBASE_API_KEY = "AIzaSyAPHkO7pUwbhta_bHUzWoa0oDZ9LrjZneM";

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

app.use(express.json());

app.post('/send-notification', async (req, res) => {
  try {
    const { token, title, body, data } = req.body;
    console.log("Received request to send notification to:", token);
    
    if (!token) {
      return res.status(400).json({ error: 'Device token is required' });
    }
    
    if (!body && !title) {
      return res.status(400).json({ error: 'At least title or body is required' });
    }
    
    const message = {
      token: token,
      notification: {
        title: title || "Hacktok", // Default title if none is provided
        body: body || "You have a new notification", // Default body if none is provided
      },
      data: data || {}
    };
    
    const response = await admin.messaging().send(message);
    console.log("Notification sent successfully:", response);
    res.json({ success: true, messageId: response });
  } catch (error) {
    console.error("Error sending notification:", error);
    res.status(500).json({ error: error.message });
  }
});

app.post('/change-password', async (req, res) => {
  const { email, currentPassword, newPassword } = req.body;
  console.log("Received request to change password for:", email);
  
  // Input validation
  if (!email || !currentPassword || !newPassword) {
    return res.status(400).json({ error: 'Email, current password, and new password are required' });
  }
  
  try {
    // First, verify the current password by attempting to sign in
    const signInResponse = await verifyCurrentPassword(email, currentPassword);
    
    if (!signInResponse.success) {
      return res.status(401).json({ error: 'Current password is incorrect' });
    }
    
    // If we get here, current password was correct
    // Get user by email
    const userRecord = await admin.auth().getUserByEmail(email);
    
    // Update the password for the user
    await admin.auth().updateUser(userRecord.uid, {
      password: newPassword,
    });
    
    // Password change successful
    res.status(200).json({ message: 'Password changed successfully' });
  } catch (error) {
    console.error('Error changing password:', error);
    if (error.code === 'auth/user-not-found') {
      res.status(404).json({ error: 'User not found' });
    } else if (error.code === 'auth/weak-password') {
      res.status(400).json({ error: 'New password is too weak' });
    } else {
      res.status(500).json({ error: 'Internal server error', details: error.message });
    }
  }
});

// Function to verify current password
async function verifyCurrentPassword(email, password) {
  try {
    const response = await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${FIREBASE_API_KEY}`,
      {
        email,
        password,
        returnSecureToken: true
      }
    );
    
    // If we got here, the password is correct
    return { success: true };
  } catch (error) {
    console.error('Password verification failed:', error.response?.data || error.message);
    return { 
      success: false, 
      error: error.response?.data?.error?.message || 'Authentication failed'
    };
  }
}

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});