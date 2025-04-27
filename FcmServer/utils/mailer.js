const nodemailer = require('nodemailer');
require('dotenv').config();

const transporter = nodemailer.createTransport({
  service: 'gmail',
  host: process.env.MAIL_HOST || 'smtp.gmail.com',
  port: process.env.MAIL_PORT || 587,
  auth: {
    user: process.env.MAIL_USER || 'your.email@gmail.com',
    pass: process.env.MAIL_PASS || 'yourpassword'
  }
});

async function sendVerificationEmail(email, link) {
  return transporter.sendMail({
    from: 'no-reply@HackTok.com',
    to: email,
    subject: 'Verify Your Email',
    html: `<p>Click to verify your account: <a href="${link}">${link}</a></p>`
  });
}

async function sendPasswordResetEmail(email, resetLink) {
  // HTML email template with HackTok branding
  const htmlContent = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Reset Your HackTok Password</title>
      <style>
        body {
          font-family: 'Segoe UI', Helvetica, Arial, sans-serif;
          margin: 0;
          padding: 0;
          color: #1c1e21;
          background-color: #f0f2f5;
        }
        .email-container {
          max-width: 600px;
          margin: 0 auto;
          background-color: #ffffff;
          border-radius: 8px;
          overflow: hidden;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
        }
        .email-header {
          background-color: #1877f2;
          padding: 20px;
          text-align: center;
        }
        .logo {
          font-size: 24px;
          font-weight: bold;
          color: white;
        }
        .email-body {
          padding: 20px;
          line-height: 1.5;
        }
        .reset-button {
          display: inline-block;
          background-color: #1877f2;
          color: white;
          text-decoration: none;
          padding: 10px 20px;
          border-radius: 6px;
          font-weight: bold;
          margin: 20px 0;
        }
        .email-footer {
          padding: 15px 20px;
          text-align: center;
          font-size: 12px;
          color: #65676b;
          border-top: 1px solid #e4e6eb;
        }
        .security-notice {
          background-color: #f0f2f5;
          padding: 10px;
          border-radius: 6px;
          font-size: 12px;
          margin-top: 20px;
        }
      </style>
    </head>
    <body>
      <div class="email-container">
        <div class="email-header">
          <div class="logo">HackTok</div>
        </div>
        <div class="email-body">
          <h2>Password Reset Request</h2>
          <p>Hello,</p>
          <p>We received a request to reset your password for your HackTok account. If you didn't make this request, you can safely ignore this email.</p>
          <p>To reset your password, click the button below:</p>
          <center>
            <a href="${resetLink}" class="reset-button">Reset Password</a>
          </center>
          <p>Or copy and paste this link into your browser:</p>
          <p style="word-break: break-all;">${resetLink}</p>
          <div class="security-notice">
            <strong>Security Tip:</strong> For your protection, this link will expire in 1 hour and can only be used once.
          </div>
        </div>
        <div class="email-footer">
          <p>© ${new Date().getFullYear()} HackTok. All rights reserved.</p>
          <p>This is an automated message, please do not reply.</p>
        </div>
      </div>
    </body>
    </html>
  `;

  return transporter.sendMail({
    from: 'no-reply@HackTok.com',
    to: email,
    subject: 'Reset Your HackTok Password',
    html: htmlContent
  });
}

async function sendVerificationCodeEmail(email, code) {
  const htmlContent = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Verify Your HackTok Account</title>
      <style>
        body {
          font-family: 'Segoe UI', Helvetica, Arial, sans-serif;
          margin: 0;
          padding: 0;
          color: #1c1e21;
          background-color: #f0f2f5;
        }
        .email-container {
          max-width: 600px;
          margin: 0 auto;
          background-color: #ffffff;
          border-radius: 8px;
          overflow: hidden;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
        }
        .email-header {
          background-color: #1877f2;
          padding: 20px;
          text-align: center;
        }
        .logo {
          font-size: 24px;
          font-weight: bold;
          color: white;
        }
        .email-body {
          padding: 20px;
          line-height: 1.5;
        }
        .verification-code {
          font-size: 32px;
          font-weight: bold;
          letter-spacing: 4px;
          margin: 20px 0;
          text-align: center;
          color: #1877f2;
        }
        .email-footer {
          padding: 15px 20px;
          text-align: center;
          font-size: 12px;
          color: #65676b;
          border-top: 1px solid #e4e6eb;
        }
        .security-notice {
          background-color: #f0f2f5;
          padding: 10px;
          border-radius: 6px;
          font-size: 12px;
          margin-top: 20px;
        }
      </style>
    </head>
    <body>
      <div class="email-container">
        <div class="email-header">
          <div class="logo">HackTok</div>
        </div>
        <div class="email-body">
          <h2>Account Verification</h2>
          <p>Hello,</p>
          <p>Thanks for signing up with HackTok! Please use the verification code below to complete your registration:</p>
          <div class="verification-code">${code}</div>
          <p>Enter this code in the app to verify your account.</p>
          <div class="security-notice">
            <strong>Security Tip:</strong> For your protection, this code will expire in 10 minutes.
          </div>
        </div>
        <div class="email-footer">
          <p>© ${new Date().getFullYear()} HackTok. All rights reserved.</p>
          <p>This is an automated message, please do not reply.</p>
        </div>
      </div>
    </body>
    </html>
  `;

  return transporter.sendMail({
    from: 'no-reply@HackTok.com',
    to: email,
    subject: 'Your HackTok Verification Code',
    html: htmlContent
  });
}

// Export the new function
module.exports = {
  sendVerificationEmail,
  sendPasswordResetEmail,
  sendVerificationCodeEmail
};