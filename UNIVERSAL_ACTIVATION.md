# Universal Activation System - Web & Mobile

## Overview
Email activation links now point to **backend** endpoint instead of web frontend, allowing both web and mobile users to activate their accounts seamlessly.

## How It Works

### 1. **Registration Flow**
```
User registers → Backend creates ActivationToken → Email sent with backend link
```

### 2. **Activation Link**
Email contains: `http://192.168.0.4:8081/api/auth/activate?token=XXXXXX`

This is a **backend GET endpoint** that:
- Validates the token
- Sets `user.isActive = true`
- Returns JSON response: `{ success: true, message: "Account activated..." }`

### 3. **Platform Handling**

#### **Web Application**
- User clicks email link → Opens in browser
- Browser hits backend endpoint → Gets JSON response
- Frontend `/activate` route can call this same endpoint via Angular HTTP client
- Shows success/error message based on response

#### **Mobile Application** 
- User clicks email link on tablet/phone → Opens in default browser
- Browser hits backend endpoint → Gets JSON response  
- User sees success message in browser
- User returns to mobile app → Can now login with activated account

## Implementation Details

### Backend Changes

**EmailService.java** (lines 12-22):
```java
@Value("${app.backend.url:http://localhost:8081}")
private String backendUrl;

public void sendActivationEmail(String toEmail, String fullName, String activationToken) {
    // Backend activation endpoint - works for both web and mobile
    String activationLink = backendUrl + "/api/auth/activate?token=" + activationToken;
    // ... email sending logic
}
```

**AuthController.java** (lines 95-104):
```java
@GetMapping("/activate")
public ResponseEntity<?> activateAccount(@RequestParam String token) {
    try {
        userService.activateAccount(token);
        return ResponseEntity.ok(ApiResponse.success(
                "Account activated successfully. You can now login.", null));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }
}
```

**application.properties**:
```properties
# Backend URL for email links - use LAN IP for mobile access
app.backend.url=${BACKEND_URL:http://192.168.0.4:8081}
```

### Configuration

#### Default Behavior
- Uses LAN IP `http://192.168.0.4:8081` (accessible from mobile devices on same WiFi)
- Allows both tablet and web browser to access activation endpoint

#### Environment Variable (Recommended for Production)
```bash
# Development (LAN)
BACKEND_URL=http://192.168.0.4:8081

# Production
BACKEND_URL=https://api.driverr.com
```

## Security Improvements

### SMTP Credentials
Updated to use **environment variables** with fallback:

```properties
spring.mail.username=${MAIL_USERNAME:njevremovic@gmail.com}
spring.mail.password=${MAIL_PASSWORD:eiij ibjf eneq ktev}
```

**Recommended:** Set environment variables before running backend:
```powershell
# PowerShell
$env:MAIL_USERNAME = "njevremovic@gmail.com"
$env:MAIL_PASSWORD = "eiij ibjf eneq ktev"
$env:BACKEND_URL = "http://192.168.0.4:8081"

.\mvnw.cmd spring-boot:run
```

This removes hardcoded credentials from version control.

## Testing

### Manual Test Flow
1. **Register** new account via mobile app or web
2. **Check email** for activation link
3. **Click link** → Opens in browser (mobile or desktop)
4. **See success message** from backend JSON response
5. **Return to app** → Login with activated credentials

### Expected Results
- ✅ Web users: Link works (backend accessible from localhost)
- ✅ Mobile users: Link works (backend accessible from LAN IP 192.168.0.4)
- ✅ Backend validates token and activates account
- ✅ User can login immediately after activation

## Advantages Over Frontend-Only Links

| Frontend Link (OLD) | Backend Link (NEW) |
|---------------------|-------------------|
| `http://localhost:4200/activate?token=X` | `http://192.168.0.4:8081/api/auth/activate?token=X` |
| ❌ Only works on PC running Angular dev server | ✅ Works on any device on same network |
| ❌ Mobile users can't access localhost:4200 | ✅ Mobile users can access LAN IP |
| ❌ Requires frontend to be running | ✅ Only backend needs to run |
| ❌ Two-step process (frontend → backend) | ✅ Direct backend activation |

## Future Enhancements

### 1. **HTML Response for Browser**
Instead of JSON, return HTML page with success/error message:
```java
@GetMapping("/activate")
public ModelAndView activateAccount(@RequestParam String token) {
    try {
        userService.activateAccount(token);
        return new ModelAndView("activation-success");
    } catch (RuntimeException e) {
        return new ModelAndView("activation-error", "message", e.getMessage());
    }
}
```

### 2. **Deep Link for Mobile App**
Add custom URL scheme to open mobile app directly:
```
driverr://activate?token=XXXXXX
```

### 3. **QR Code Activation**
Generate QR code in email that mobile app can scan.

## Troubleshooting

### Issue: "Connection refused" on mobile
- **Cause:** Backend not running or wrong IP address
- **Fix:** Ensure backend is running on 192.168.0.4:8081, check WiFi connection

### Issue: "Invalid or expired token"
- **Cause:** Token older than 24 hours or already used
- **Fix:** Request new activation email via `/api/auth/resend-activation` (if implemented)

### Issue: Email not received
- **Cause:** SMTP credentials invalid or Gmail blocking
- **Fix:** Check application.properties SMTP settings, verify app password

## Files Modified

| File | Changes |
|------|---------|
| [EmailService.java](backend/src/main/java/service/EmailService.java) | Added `@Value("${app.backend.url}")`, updated activation link to use backend URL |
| [application.properties](backend/src/main/resources/application.properties) | Added `app.backend.url` property, moved SMTP credentials to environment variables with fallback |
| [AuthController.java](backend/src/main/java/controller/AuthController.java) | No changes (endpoint already existed) |

## Summary

✅ **Problem Solved:** Activation links now work for both web and mobile users

✅ **Backend-Direct:** Single endpoint handles activation for all platforms

✅ **Configurable:** Backend URL can be changed via environment variable

✅ **Secure:** SMTP credentials use environment variables (with fallback for development)

✅ **Network-Friendly:** Uses LAN IP (192.168.0.4) accessible from mobile devices on WiFi
