const express = require('express');
const admin = require('../firebase');
const router = express.Router();

router.post('/send-notification', async (req, res) => {
  try {
    const { token, title, body, data } = req.body;
    console.log('Received notification request:', req.body);
    if (!token) return res.status(400).json({ error: 'Device token is required' });
    if (!body && !title) return res.status(400).json({ error: 'At least title or body is required' });

    const message = {
      token,
      notification: {
        title: title || 'Hacktok',
        body: body || 'You have a new notification',
      },
      data: data || {},
    };

    const response = await admin.messaging().send(message);
    res.json({ success: true, messageId: response });
  } catch (error) {
    console.error('Error sending notification:', error);
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;