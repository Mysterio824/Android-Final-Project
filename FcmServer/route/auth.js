const express = require('express');
const jwt = require('jsonwebtoken');
const admin = require('../firebase');
const { sendVerificationCodeEmail, sendPasswordResetEmail } = require('../utils/mailer');
const cache = require('../utils/cache');
const createUserData = require('../utils/createUserData');
const verifyCurrentPassword = require('../utils/verifyCurrentPassword');

const router = express.Router();
const JWT_SECRET = process.env.JWT_SECRET || 'secret_key';


router.post('/signup', async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) return res.status(400).json({ error: 'Email and password required' });

  if (cache.has(email)) return res.status(409).json({ error: 'Email is already in verification process' });

  try {
    await admin.auth().getUserByEmail(email);
    return res.status(409).json({ error: 'Email already in use' });
  } catch (err) {
    if (err.code !== 'auth/user-not-found') return res.status(500).json({ error: 'Server error' });
  }

  // Generate a 6-digit verification code
  const verificationCode = Math.floor(100000 + Math.random() * 900000).toString();
  
  // Store email, password and code in cache
  cache.set(email, { email, password, verificationCode });

  try {
    // Send verification code via email
    await sendVerificationCodeEmail(email, verificationCode);
    res.status(200).json({ message: 'Verification code sent to your email' });
  } catch (error) {
    cache.del(email);
    res.status(500).json({ error: 'Failed to send email', details: error.message });
  }
});

// Add this endpoint to verify the code
router.post('/verify-code', async (req, res) => {
  const { email, code } = req.body;
  
  if (!email || !code) {
    return res.status(400).json({ error: 'Email and verification code required' });
  }

  const cachedData = cache.get(email);
  if (!cachedData) {
    return res.status(400).json({ error: 'Verification expired or invalid email' });
  }

  if (cachedData.verificationCode !== code) {
    return res.status(400).json({ error: 'Invalid verification code' });
  }

  try {
    // Create the user in Firebase
    const userRecord = await admin.auth().createUser({
      email: cachedData.email,
      password: cachedData.password
    });
    
    // Create additional user data
    await createUserData(userRecord);
    
    // Remove the cache entry
    cache.del(email);
    
    return res.status(200).json({ verified: true, message: 'Account verified successfully' });
  } catch (error) {
    return res.status(500).json({ error: 'Failed to create user', details: error.message });
  }
});

// Endpoint to resend verification code
router.post('/resend-code', async (req, res) => {
  const { email } = req.body;
  
  if (!email) {
    return res.status(400).json({ error: 'Email required' });
  }

  const cachedData = cache.get(email);
  if (!cachedData) {
    return res.status(400).json({ error: 'No pending verification for this email' });
  }

  // Generate a new verification code
  const verificationCode = Math.floor(100000 + Math.random() * 900000).toString();
  
  // Update the cached data with the new code
  cache.set(email, { ...cachedData, verificationCode });

  try {
    // Send the new code
    await sendVerificationCodeEmail(email, verificationCode);
    return res.status(200).json({ message: 'Verification code resent' });
  } catch (error) {
    return res.status(500).json({ error: 'Failed to send email', details: error.message });
  }
});

// Endpoint to set username after verification
router.post('/set-username', async (req, res) => {
  const { email, username } = req.body;
  
  if (!email || !username) {
    return res.status(400).json({ error: 'Email and username required' });
  }

  try {
    // Get the user by email
    const userRecord = await admin.auth().getUserByEmail(email);
    
    // Update user data with username
    await admin.firestore().collection('users').doc(userRecord.uid).update({
      username: username,
      displayName: username
    });
    
    return res.status(200).json({ success: true, message: 'Username set successfully' });
  } catch (error) {
    return res.status(500).json({ error: 'Failed to set username', details: error.message });
  }
});

router.post('/change-password', async (req, res) => {
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

router.post('/reset-password', async (req, res) => {
  const { email } = req.body;
  console.log('Received request to reset password for:', email);

  if (!email) return res.status(400).json({ error: 'Email is required' });

  try {
    // Generate a password reset link using Firebase
    const resetLink = await admin.auth().generatePasswordResetLink(email);
    
    // Send our custom-styled email with the Firebase-generated reset link
    await sendPasswordResetEmail(email, resetLink);
    
    // Return success response
    res.status(200).json({ message: 'Send request successfully' });
  } catch (error) {
    console.error('Error sending password reset email:', error);
    
    if (error.code === 'auth/user-not-found') {
      // For security reasons, still return 200 even if user doesn't exist
      return res.status(200).json({ message: 'Send request successfully' });
    } else {
      return res.status(500).json({ error: 'Failed to send password reset email', details: error.message });
    }
  }
});

module.exports = router;