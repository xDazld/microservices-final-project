# Login System Guide

## Overview

The DNS Shield application now includes a complete authentication system with JWT-based login for
administrators and users.

## Quick Start

### Default Credentials

For development/testing, the following credentials are available:

| Username | Password | Role  | Access Level                          |
|----------|----------|-------|---------------------------------------|
| `admin`  | `admin`  | admin | Full access to all features           |
| `user`   | `user`   | user  | Read access, limited write operations |

## Login Flow

### 1. Access the Login Page

Navigate to: `http://localhost:8080/ui/login`

### 2. Enter Credentials

- Username: `admin`
- Password: `admin`

### 3. Upon Successful Login

- JWT token is stored in `sessionStorage`
- User is redirected to the dashboard (or the page they were trying to access)
- Navigation shows username and logout button
- Protected pages become accessible

### 4. Authentication State

The frontend automatically:

- Includes JWT token in all API requests
- Redirects to login when accessing protected pages without authentication
- Shows/hides UI elements based on role (admin/user)
- Displays user info in the header

## Protected Pages

### Admin Only

- `/ui/admin` - Administration panel
- System information endpoint

### User + Admin

- `/ui/agent` - AI Agent features
- Detailed cache and security statistics

### Public (No Authentication Required)

- `/ui` - Dashboard (view only)
- `/ui/filters` - Filter rules (view only)
- `/ui/query` - DNS query tool
- `/ui/login` - Login page

## API Endpoints

### Authentication Endpoint

```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}
```

**Response (Success):**

```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "admin",
  "expiresIn": 3600
}
```

**Response (Error):**

```json
{
  "error": "Invalid username or password"
}
```

### Using the Token

Include the JWT token in the `Authorization` header for all authenticated requests:

```bash
curl -X POST http://localhost:8080/api/v1/filters \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Rule","pattern":"*.test.com","type":"BLOCK","category":"custom","priority":50}'
```

## Frontend Integration

### JavaScript Authentication Functions

The frontend (`/js/app.js`) provides these authentication functions:

#### `authenticatedFetch(url, options)`

Wrapper around `fetch()` that automatically includes JWT token:

```javascript
const response = await authenticatedFetch('/api/v1/admin/stats');
```

#### `isAuthenticated()`

Check if user is logged in:

```javascript
if (isAuthenticated()) {
    // Show admin features
}
```

#### `logout()`

Clear session and redirect to login:

```javascript
logout(); // Called when user clicks logout button
```

#### `updateAuthState()`

Update UI based on authentication status (called automatically):

```javascript
updateAuthState(); // Shows username, logout button, etc.
```

## Token Information

### JWT Claims

The generated JWT token includes:

```json
{
  "iss": "https://dns-shield.local",
  "upn": "admin",
  "groups": [
    "admin"
  ],
  "exp": 1735689600
}
```

- **iss**: Issuer (configured in application.properties)
- **upn**: User Principal Name (username)
- **groups**: User roles (used for @RolesAllowed)
- **exp**: Expiration timestamp (1 hour from login)

### Token Expiration

- **Duration**: 1 hour (3600 seconds)
- **Storage**: `sessionStorage` (cleared when browser tab closes)
- **Refresh**: Users must login again after expiration

## Security Considerations

### Development vs Production

**Current Setup (Development):**

- Hardcoded credentials in `AuthResource.java`
- Simple RSA key for JWT signing
- Permissive CORS policy
- No password hashing

**Production Requirements:**

1. **Database Authentication**
    - Store users in database
    - Hash passwords using bcrypt/argon2
    - Implement account lockout after failed attempts

2. **Secure Key Management**
    - Generate proper RSA key pair
    - Store private key securely (environment variable, key vault)
    - Rotate keys periodically

3. **Enhanced Security**
    - Implement refresh tokens
    - Add rate limiting on login endpoint
    - Use HTTPS only
    - Restrict CORS origins
    - Add CSRF protection

4. **Session Management**
    - Implement token revocation
    - Track active sessions
    - Allow admin to force logout users

## Customization

### Adding New Users

Edit `AuthResource.java`:

```java
private UserInfo validateCredentials(String username, String password) {
	// Add new user
	if ("newuser".equals(username) && "password".equals(password)) {
		return new UserInfo("newuser", "user");
	}
	
	// Existing users...
}
```

### Changing Token Expiration

Edit `AuthResource.java`:

```java
return Jwt.issuer(issuer)
        .

upn(userInfo.username)
        .

groups(groups)
        .

expiresIn(Duration.ofHours(24)) // Change to 24 hours
		.

sign();
```

### Customizing JWT Issuer

Edit `application.properties`:

```properties
mp.jwt.verify.issuer=https://your-domain.com
```

## Troubleshooting

### "Invalid username or password"

- Check credentials match exactly (case-sensitive)
- Verify the credentials in `AuthResource.validateCredentials()`

### "Authentication required" / Redirect to login

- Token may have expired (check browser console)
- Token not being sent with request
- Server restarted (tokens invalidated)

### Token not persisting

- Check browser's sessionStorage
- Ensure JavaScript is enabled
- Check for browser extensions blocking storage

### 401 Unauthorized on API calls

- Token expired
- Token format invalid
- Token not included in Authorization header
- Wrong issuer configuration

### Can't access admin pages

- User role is "user" not "admin"
- Login with admin credentials
- Check JWT token groups claim

## Testing Authentication

### Test Admin Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

### Test Protected Endpoint

```bash
# Get token from login response
TOKEN="eyJhbGci..."

# Use token in request
curl http://localhost:8080/api/v1/admin/stats \
  -H "Authorization: Bearer $TOKEN"
```

### Test Token Expiration

```bash
# Wait 1 hour or modify expiration in code
# Then try to use the old token - should get 401
curl http://localhost:8080/api/v1/admin/stats \
  -H "Authorization: Bearer $EXPIRED_TOKEN"
```

## Files Modified/Created

### New Files

- `src/main/java/dev/pacr/dns/api/AuthResource.java` - Login endpoint
- `src/main/resources/templates/login.qute.html` - Login page
- `src/main/resources/privateKey.pem` - JWT signing key (dev only)
- `LOGIN.md` - This documentation

### Modified Files

- `src/main/java/dev/pacr/dns/api/FrontendResource.java` - Added login route
- `src/main/resources/META-INF/resources/js/app.js` - Authentication functions
- `src/main/resources/templates/layout.qute.html` - Login link
- `src/main/resources/application.properties` - JWT configuration

## Next Steps

For production deployment:

1. Generate proper RSA key pair using OpenSSL
2. Implement database-backed user management
3. Add password hashing (bcrypt)
4. Implement refresh tokens
5. Add user registration endpoint
6. Implement forgot password functionality
7. Add 2FA/MFA support
8. Set up proper logging and audit trails

