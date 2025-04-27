const express = require('express');
const router = express.Router();
const crypto = require('crypto');

// Lưu trữ key trong memory (trong thực tế nên lưu trong database)
let encryptionKey = null;

// Tạo key mới nếu chưa có
function generateKey() {
    if (!encryptionKey) {
        encryptionKey = crypto.randomBytes(32).toString('base64');
    }
    return encryptionKey;
}

// Endpoint để lấy key
router.get('/encryption-key', (req, res) => {
    try {
        const key = generateKey();
        res.json({ key });
    } catch (error) {
        res.status(500).json({ error: 'Failed to generate encryption key' });
    }
});

// Endpoint để tạo key mới (chỉ dùng trong môi trường development)
router.post('/encryption-key/refresh', (req, res) => {
    try {
        encryptionKey = crypto.randomBytes(32).toString('base64');
        res.json({ message: 'Key refreshed successfully' });
    } catch (error) {
        res.status(500).json({ error: 'Failed to refresh encryption key' });
    }
});

module.exports = router; 