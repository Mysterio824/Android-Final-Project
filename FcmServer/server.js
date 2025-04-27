const express = require('express');
const authRoutes = require('./route/auth');
const notificationRoutes = require('./route/notify');
const encryptionRoutes = require('./route/encryption');

const app = express();
app.use(express.json());

app.use('/', authRoutes);
app.use('/', notificationRoutes);
app.use('/', encryptionRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});