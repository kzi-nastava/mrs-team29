# Email Service Testing Scenarios

## Test Scenario 1: Complete Registration & Activation Flow

### Prerequisites
- Backend running on http://localhost:8081
- Frontend running on http://localhost:4200
- Gmail SMTP configured in application.properties

### Steps
1. **Navigate to Registration**
   - Go to http://localhost:4200/register
   
2. **Fill Registration Form**
   ```
   First Name: John
   Last Name: Doe
   Email: testuser@gmail.com (use your actual test email)
   Phone Number: +381645123456
   Address: Bulevar Svetog Save 71, Zemun, Beograd
   Password: TestPass123
   Confirm Password: TestPass123
   ```

3. **Submit Registration**
   - Click "Create Account" button
   - Page should show success message
   - Auto-redirect to login page

4. **Check Email**
   - Check testuser@gmail.com inbox
   - Look for email from "noreply@driverr.com"
   - Subject: "Activate Your Driverr Account"

5. **Click Activation Link**
   - Email contains link: `http://localhost:4200/activate?token=XXXXX`
   - Click the link

6. **Verify Activation**
   - Page shows: "Account Activated Successfully!"
   - Click "Go to Login" button

7. **Login with Activated Account**
   - Email: testuser@gmail.com
   - Password: TestPass123
   - Should successfully log in

## Test Scenario 2: Password Reset Flow

### Prerequisites
- User account exists and is activated
- Backend and frontend running

### Steps
1. **Navigate to Forgot Password**
   - Go to http://localhost:4200/forgot-password
   
2. **Request Password Reset**
   - Enter email: testuser@gmail.com
   - Click "Send Reset Link"
   - Page shows: "Password reset link has been sent to your email"

3. **Check Email**
   - Check testuser@gmail.com inbox
   - Look for email from "noreply@driverr.com"
   - Subject: "Reset Your Driverr Password"

4. **Click Reset Link**
   - Email contains link: `http://localhost:4200/reset-password?token=XXXXX`
   - Click the link

5. **Reset Password**
   - New Password: NewPass456
   - Confirm Password: NewPass456
   - Click "Reset Password" button
   - Page shows: "Password reset successfully! Redirecting to login..."

6. **Login with New Password**
   - Email: testuser@gmail.com
   - Password: NewPass456
   - Should successfully log in

## Test Scenario 3: Error Handling - Invalid Activation Token

### Prerequisites
- Already have an activated account

### Steps
1. **Navigate with Invalid Token**
   - Go to: http://localhost:4200/activate?token=invalid-token-12345
   
2. **Expected Result**
   - Page shows: "Activation Failed"
   - Error message: "Invalid activation token"
   - Button to "Go to Login"

## Test Scenario 4: Error Handling - Expired Activation Token

### Prerequisites
- Wait for activation token to expire (24 hours)
- Or manually update database to set expiry to past time

### Steps
1. **Navigate with Expired Token**
   - Use a valid token from an older registration
   
2. **Expected Result**
   - Page shows: "Activation Failed"
   - Error message: "Activation token has expired"

## Test Scenario 5: Error Handling - Already Used Token

### Prerequisites
- Have used an activation token once

### Steps
1. **Try to Use Token Again**
   - Navigate to same activation link in browser history
   
2. **Expected Result**
   - Page shows: "Activation Failed"
   - Error message: "Activation token already used"

## Test Scenario 6: Error Handling - Invalid Email

### Prerequisites
- Account is not registered with this email

### Steps
1. **Request Password Reset**
   - Go to http://localhost:4200/forgot-password
   - Enter: nonexistent@gmail.com
   - Click "Send Reset Link"

2. **Expected Result**
   - Error message: "User not found"
   - No email is sent

## Test Scenario 7: Validation - Password Mismatch

### Steps
1. **Navigate to Reset Password**
   - Use any valid reset token: http://localhost:4200/reset-password?token=XXXXX
   
2. **Enter Mismatched Passwords**
   ```
   New Password: TestPass123
   Confirm Password: DifferentPass456
   ```

3. **Expected Result**
   - Error text shows: "Passwords do not match"
   - Submit button is disabled (grey)

## Test Scenario 8: Validation - Password Too Short

### Steps
1. **Reset Password with Short Password**
   ```
   New Password: Test (only 4 characters)
   Confirm Password: Test
   ```

2. **Expected Result**
   - Error text shows: "Password must be at least 6 characters"
   - Submit button is disabled

## Test Scenario 9: Multiple Registration Attempts

### Steps
1. **Register with Email**
   - Email: duplicate@gmail.com
   
2. **Try to Register Again**
   - Same email: duplicate@gmail.com
   
3. **Expected Result**
   - Error message: "Email already registered"
   - Cannot register with same email twice

## Email Content Examples

### Activation Email
```
From: noreply@driverr.com
To: testuser@gmail.com
Subject: Activate Your Driverr Account

Dear John Doe,

Thank you for registering with Driverr! To complete your registration and activate your account, 
please click the link below.

Activation Link: http://localhost:4200/activate?token=550e8400-e29b-41d4-a716-446655440000

This link will expire in 24 hours.

If you did not create this account, please ignore this email.

Best regards,
Driverr Team
```

### Password Reset Email
```
From: noreply@driverr.com
To: testuser@gmail.com
Subject: Reset Your Driverr Password

Dear John Doe,

We received a request to reset your password. To proceed with resetting your password, 
please click the link below.

Reset Link: http://localhost:4200/reset-password?token=660e8400-e29b-41d4-a716-446655440000

This link will expire in 1 hour.

If you did not request a password reset, please ignore this email.

Best regards,
Driverr Team
```

## Debugging Tips

### View Backend Logs
- Check console for "Email sent successfully to..." messages
- Or "Failed to send email:" with error details

### Check Email Service Status
1. Look for these messages in backend logs:
   ```
   Email sent successfully to testuser@gmail.com
   Activation email sent to testuser@gmail.com
   Password reset email sent to testuser@gmail.com
   ```

2. If you see errors like:
   ```
   Failed to send email: 534 5.7.9 Please log in with your app password...
   ```
   - Update Gmail app password in application.properties
   - Regenerate new app password from Google account

### Frontend Component Debugging
1. Check browser console (F12) for errors
2. Check Network tab to see API calls
3. Verify routes are loaded: http://localhost:4200/activate, etc.

## Performance Notes

- Activation tokens: Valid for 24 hours
- Password reset tokens: Valid for 1 hour
- Email delivery: Usually 1-5 seconds
- Token validation: Instant (checked in database)

## Security Considerations

✅ **Implemented**
- Tokens are UUIDs (impossible to guess)
- Tokens expire automatically
- Tokens can only be used once
- Database stores tokens, not passwords
- Email addresses verified before account creation

⚠️ **Production Recommendations**
- Use HTTPS for all pages (not localhost)
- Implement rate limiting on password reset
- Add captcha to password reset form
- Implement email bounce handling
- Add logging for all email operations
- Use transactional email service (SendGrid, AWS SES)
- Implement audit trail for account changes
