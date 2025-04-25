const express = require('express');
const authRoutes = require('./route/auth');
const notificationRoutes = require('./route/notify');

const app = express();
app.use(express.json());

app.use('/', authRoutes);
app.use('/', notificationRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});