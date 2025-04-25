const express = require('express');
const jwt = require('jsonwebtoken');
const admin = require('../firebase');
const { sendVerificationEmail, sendPasswordResetEmail } = require('../utils/mailer');
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

  const token = jwt.sign({ email, password }, JWT_SECRET, { expiresIn: '10m' });
  cache.set(email, { email, password });

  const verifyLink = `http://localhost:3000/verify/${token}`;
  try {
    await sendVerificationEmail(email, verifyLink);
    res.status(200).json({ message: 'Verification email sent' });
  } catch (error) {
    cache.del(email);
    res.status(500).json({ error: 'Failed to send email', details: error.message });
  }
});

router.get('/verify/:token', async (req, res) => {
  try {
    const decoded = jwt.verify(req.params.token, JWT_SECRET);
    const cachedData = cache.get(decoded.email);

    if (!cachedData) return res.status(400).json({ error: 'Verification expired or already used' });

    const userRecord = await admin.auth().createUser({
      email: cachedData.email,
      password: cachedData.password
    });
    await createUserData(userRecord);

    cache.del(decoded.email);
    res.status(200).json({ message: 'Account verified and created', uid: userRecord.uid });
  } catch (error) {
    res.status(400).json({ error: 'Invalid or expired token' });
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