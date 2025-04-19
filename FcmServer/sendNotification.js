const express = require('express');
const admin = require('firebase-admin');
const serviceAccount = require("./android-91bea-firebase-adminsdk-fbsvc-926d39bbea.json"); // Your service account path

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const app = express();
app.use(express.json());

app.post('/send-notification', async (req, res) => {
  try {
    const { token, title, body, data } = req.body;
    console.log("Received request to send notification to:", token);
    
    if (!token) {
      return res.status(400).json({ error: 'Device token is required' });
    }

    if (!body) {
      return res.status(400).json({ error: 'At least title or body is required' });
    }
    
    const message = {
      token: token,
      notification: {
        title: title || "Hacktok",
        body: body,
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

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});