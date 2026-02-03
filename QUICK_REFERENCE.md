# Quick Reference Guide

## Quick Start Commands

### 1. Configure Email (First Time Only)
```bash
# Edit application.properties
# File: backend/src/main/resources/application.properties
# 
# Replace:
# spring.mail.username=your-email@gmail.com
# spring.mail.password=your-16-char-app-password

# Get Gmail app password:
# 1. Go to https://myaccount.google.com/apppasswords
# 2. Select "Mail" and "Windows Computer"
# 3. Copy the 16-character password
# 4. Paste in application.properties (no spaces)
```

### 2. Build Backend
```bash
cd backend
./mvnw.cmd clean package -DskipTests
```

### 3. Run Backend
```bash
cd backend
.\mvnw.cmd spring-boot:run
# OR
java -jar target/Driverr-0.0.1-SNAPSHOT.jar
# Server will start on http://localhost:8081
```

### 4. Build Frontend
```bash
cd frontend/web/driverr-ui
npm run build
```

### 5. Run Frontend (Development)
```bash
cd frontend/web/driverr-ui
npm start
# App will open at http://localhost:4200
```

### 6. Run Frontend (Production - Built Files)
```bash
cd frontend/web/driverr-ui
npx http-server dist/driverr-ui -p 4200
```

## Email Testing

### Test Registration & Activation
```
1. Go to: http://localhost:4200/register
2. Fill form with test data
3. Use a real email you have access to
4. Click "Create Account"
5. Check email inbox
6. Click activation link
7. Success page appears
8. Click "Go to Login"
9. Login with your credentials
```

### Test Password Reset
```
1. Go to: http://localhost:4200/forgot-password
2. Enter your email
3. Click "Send Reset Link"
4. Check email inbox
5. Click reset link
6. Enter new password (min 6 chars)
7. Confirm password (must match)
8. Click "Reset Password"
9. Auto-redirect to login
10. Login with new password
```

## API Endpoints (For Testing with Postman/curl)

### Register User
```bash
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "email": "newuser@gmail.com",
  "password": "TestPass123",
  "confirmPassword": "TestPass123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+381645123456",
  "address": "Bulevar Svetog Save 71"
}
```

### Activate Account
```bash
GET http://localhost:8081/api/auth/activate?token=ACTIVATION_TOKEN_HERE
```

### Request Password Reset
```bash
POST http://localhost:8081/api/auth/password-reset/request
Content-Type: application/json

{
  "email": "user@gmail.com"
}
```

### Reset Password
```bash
POST http://localhost:8081/api/auth/password-reset/reset
Content-Type: application/json

{
  "token": "RESET_TOKEN_HERE",
  "newPassword": "NewPassword456",
  "confirmPassword": "NewPassword456"
}
```

### Login
```bash
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "email": "user@gmail.com",
  "password": "TestPass123"
}
```

## Key Routes (Frontend)

| Route | Purpose | Status |
|-------|---------|--------|
| `/` | Redirects to login | Active |
| `/login` | User login | Active |
| `/register` | User registration | Active |
| `/activate?token=X` | Email verification | NEW ✅ |
| `/forgot-password` | Password reset request | NEW ✅ |
| `/reset-password?token=X` | Password reset form | NEW ✅ |
| `/profile` | User profile (authenticated) | Existing |
| `/driver-activate` | Driver approval | Existing |
| `/admin/approvals` | Admin dashboard | Existing |

## Directory Structure

```
backend/
├── src/main/java/
│   ├── service/
│   │   ├── EmailService.java (NEW)
│   │   ├── UserService.java
│   │   └── impl/
│   │       └── UserServiceImpl.java (UPDATED)
│   ├── controller/
│   │   └── AuthController.java
│   ├── domain/
│   ├── dto/
│   ├── repository/
│   └── main/
├── src/main/resources/
│   └── application.properties (UPDATED)
└── pom.xml (UPDATED)

frontend/web/driverr-ui/src/app/
├── pages/
│   ├── activate/ (NEW)
│   ├── request-password-reset/ (NEW)
│   ├── reset-password/ (NEW)
│   ├── login/
│   ├── register/
│   └── ...
├── services/
│   └── auth.service.ts
├── guards/
│   └── auth.guard.ts
├── app.routes.ts (UPDATED)
└── ...
```

## Configuration Files

### application.properties - Email Settings
```properties
# Gmail SMTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Alternative Email Providers

#### Outlook/Office365
```properties
spring.mail.host=smtp.office365.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

#### SendGrid
```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=SG.YOUR_SENDGRID_API_KEY
```

#### Custom Mail Server
```properties
spring.mail.host=your-mail-server.com
spring.mail.port=587
spring.mail.username=your-username
spring.mail.password=your-password
```

## Troubleshooting Quick Fixes

### Backend Won't Start
```bash
# Clear compiled files and rebuild
cd backend
rm -r target
.\mvnw.cmd clean install -DskipTests
.\mvnw.cmd spring-boot:run
```

### Frontend Build Errors
```bash
cd frontend/web/driverr-ui
rm -r node_modules dist
npm install
npm run build
```

### Email Not Sending
```
1. Check Gmail app password is correct (16 chars, no spaces)
2. Check 2-Factor Authentication is enabled on Google account
3. Check firewall isn't blocking port 587
4. See backend console for error messages
5. Verify application.properties has correct credentials
```

### Activation Link Not Working
```
1. Check token in URL matches database
2. Check token hasn't expired (24 hours)
3. Check token hasn't been used already
4. See backend logs for detailed error
5. Try registering again with new email
```

### Password Reset Not Working
```
1. Check email address in request form
2. Check reset link is from latest email
3. Check token hasn't expired (1 hour)
4. Verify passwords match in reset form
5. Ensure new password is min 6 characters
```

## Database Queries (Debugging)

### Check User Activation Status
```sql
SELECT id, email, first_name, last_name, is_activated 
FROM "user" 
WHERE email = 'test@gmail.com';
```

### Check Activation Tokens
```sql
SELECT token, user_id, expires_at, is_used 
FROM activation_token 
WHERE user_id = 'USER_ID_HERE' 
ORDER BY created_at DESC;
```

### Check All Pending Activations
```sql
SELECT u.id, u.email, u.first_name, at.token, at.expires_at
FROM "user" u
LEFT JOIN activation_token at ON u.id = at.user_id
WHERE u.is_activated = false
AND at.is_used = false
AND at.expires_at > NOW();
```

## Performance Tips

- Activation tokens expire after 24 hours (configurable)
- Password reset tokens expire after 1 hour (configurable)
- Email sending is async (doesn't block registration)
- Frontend validation prevents unnecessary API calls
- Backend caches database lookups efficiently

## Security Checklist

✅ Passwords hashed in transit (HTTPS in production)
✅ Tokens are UUID (impossible to guess)
✅ Tokens expire (24h for activation, 1h for reset)
✅ Tokens can only be used once
✅ Email addresses verified before account use
✅ Password complexity enforced (min 6 chars)
✅ Form validation on frontend AND backend
✅ SQL injection prevention (parameterized queries)
✅ CSRF protection (Spring Security)

## Monitoring Commands

### Monitor Backend Logs (Real-time)
```bash
# While backend is running, see email logs:
# Look for:
# "Email sent successfully to..."
# "Activation email sent to..."
# "Password reset email sent to..."
# "Failed to send email:..."
```

### Monitor Frontend (Browser Console)
```javascript
// Check auth service calls
localStorage.getItem('token') // See stored token
localStorage.getItem('user')  // See stored user info

// Check API calls in Network tab (F12)
// POST /api/auth/register
// GET /api/auth/activate
// POST /api/auth/password-reset/request
// POST /api/auth/password-reset/reset
```

## Useful npm Scripts

```bash
cd frontend/web/driverr-ui

npm run build          # Build for production
npm start             # Start dev server (auto-reload)
npm test              # Run unit tests
npm run lint          # Run linter
npm run format        # Format code
```

## Useful Maven Commands

```bash
cd backend

.\mvnw.cmd clean install      # Full build
.\mvnw.cmd clean compile      # Compile only
.\mvnw.cmd clean package      # Package JAR
.\mvnw.cmd spring-boot:run    # Run app
.\mvnw.cmd test               # Run tests
```

## Documentation Files

1. **EMAIL_SERVICE_SUMMARY.md** - Overview of everything (START HERE)
2. **EMAIL_SERVICE_IMPLEMENTATION.md** - Technical details
3. **EMAIL_SERVICE_SETUP.md** - Configuration guide
4. **EMAIL_SERVICE_TESTING.md** - Test scenarios
5. **QUICK_REFERENCE.md** - This file

---

## Remember

- Always configure email credentials in application.properties before running
- Use a test email address you have access to
- Check email spam/junk folder if email doesn't arrive
- Frontend and backend must both be running
- Backend on port 8081, frontend on port 4200
- Ctrl+C to stop running servers

Questions? Check the documentation files first! 📖
