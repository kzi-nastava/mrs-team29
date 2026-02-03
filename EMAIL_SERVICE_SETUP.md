# Email Service Setup Guide

## Quick Start

Your email service is now fully integrated! Follow these steps to enable real email sending:

### Step 1: Get Gmail App Password

1. Go to your Google Account: https://myaccount.google.com
2. Click "Security" in the left menu
3. Enable "2-Step Verification" if not already enabled
4. Go to "App passwords" (near the bottom)
5. Select "Mail" and "Windows Computer"
6. Google will generate a 16-character password (e.g., `abcd efgh ijkl mnop`)

### Step 2: Update Configuration

Edit: `backend/src/main/resources/application.properties`

Replace these lines:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

With your actual Gmail and the 16-character app password:
```properties
spring.mail.username=myemail@gmail.com
spring.mail.password=abcdefghijklmnop
```

### Step 3: Restart Backend

Stop any running backend instance and rebuild:
```bash
cd backend
./mvnw.cmd clean package -DskipTests
```

## Testing the Email Service

### Register a New Account
1. Go to http://localhost:4200/register
2. Fill in all fields with test data
3. Submit the form
4. Check your email inbox for the activation email

### Activation Email Content
- Subject: "Activate Your Driverr Account"
- Contains: Activation link with 24-hour expiration
- Link format: `http://localhost:4200/activate?token=XXXXX`

### Complete Activation
1. Click the activation link in the email
2. You'll see the activation page at `/activate?token=XXXXX`
3. Page will show success message
4. Click "Go to Login" button
5. Now you can log in with your registered credentials

### Test Password Reset
1. Go to http://localhost:4200/login
2. Click "Forgot Password" (if you add this link)
3. Or navigate directly to http://localhost:4200/forgot-password
4. Enter your email
5. Check your email for the password reset link
6. Click the link to reset your password

## File Locations

```
backend/
├── pom.xml (spring-boot-starter-mail added)
├── src/main/java/service/
│   └── EmailService.java (NEW)
├── src/main/java/service/impl/
│   └── UserServiceImpl.java (UPDATED)
└── src/main/resources/
    └── application.properties (UPDATED)

frontend/web/driverr-ui/src/app/pages/
├── activate/ (NEW)
│   └── activate.component.ts
├── request-password-reset/ (NEW)
│   └── request-password-reset.component.ts
├── reset-password/ (NEW)
│   └── reset-password.component.ts
└── app.routes.ts (UPDATED)
```

## Architecture

```
User Registration Flow:
┌─────────────┐
│  Register   │
│  Component  │
└──────┬──────┘
       │ POST /api/auth/register
       ▼
┌──────────────────┐
│ UserServiceImpl   │
│  .registerUser() │
└──────┬───────────┘
       │ Creates ActivationToken
       ├─────────────────────────────┐
       │                             │
       ▼                             ▼
┌─────────────────┐          ┌──────────────────┐
│  User Entity    │          │ EmailService     │
│  (isActivated   │          │ .sendActivation  │
│   = false)      │          │ Email()          │
└─────────────────┘          └──────┬───────────┘
                                    │
                                    ▼
                            ┌──────────────────┐
                            │   Gmail SMTP     │
                            │  (real email)    │
                            └──────┬───────────┘
                                   │
                                   ▼
                            ┌──────────────────┐
                            │  User's Inbox    │
                            │  (activation)    │
                            └──────────────────┘
```

## Troubleshooting

### "Failed to send email" Error
- Check that Gmail app password is correct (remove spaces)
- Ensure 2-Factor Authentication is enabled on Google account
- Verify SMTP settings in application.properties are correct
- Check firewall/antivirus isn't blocking port 587

### Email Not Received
- Check spam/junk folder
- Wait a few minutes (emails can take time)
- Check email address is spelled correctly in form
- See console logs for error messages

### Backend Won't Start
- Ensure JavaMailSender configuration is valid
- Check that spring-boot-starter-mail is in pom.xml
- Check for syntax errors in application.properties

### Links Not Working in Email
- Make sure frontend is running on http://localhost:4200
- If deployed to different URL, update links in EmailService.java
- Check that the token is correctly passed in URL

## Features Implemented

✅ **Account Activation**
- 24-hour activation tokens
- Email verification required before login
- Automatic token expiration
- Prevents token reuse

✅ **Password Reset**
- 1-hour reset token expiration
- Secure token-based reset flow
- Password confirmation validation
- Email notification of reset

✅ **Frontend Pages**
- Activation page with real-time validation
- Password reset request form
- Password reset completion form
- Error handling and user feedback

✅ **Security**
- Token expiration enforcement
- One-time use tokens
- Client-side password validation
- Server-side validation on all operations

## Production Considerations

Before deploying to production:
1. Use environment variables for email credentials
2. Implement email template HTML for better formatting
3. Add rate limiting to password reset requests
4. Implement email verification on profile updates
5. Add admin interface to resend activation emails
6. Implement logging for all email operations
7. Set up email bounce handling
8. Use dedicated email service (SendGrid, AWS SES, etc.)

## Support

For issues or questions:
1. Check the console logs for error messages
2. Verify email credentials in application.properties
3. Test with a simple Gmail account first
4. Check that backend is running on port 8081
5. Check that frontend is running on port 4200
