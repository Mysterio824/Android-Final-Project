const axios = require('axios');
require('dotenv').config();

const FIREBASE_API_KEY = process.env.FIREBASE_API_KEY || 'your-default-api-key';

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

module.exports = verifyCurrentPassword;