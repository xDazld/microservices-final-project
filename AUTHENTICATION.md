# Authentication and Authorization Guide

## Overview

The DNS Shield application implements role-based access control (RBAC) using JWT tokens. The system
has two roles:

- **admin** - Full access to all features including system administration
- **user** - Read access to most features, limited write access

## Authentication Requirements by Endpoint

### Public Endpoints (No Authentication Required)

#### Static Resources

- `/css/*` - CSS stylesheets
- `/js/*` - JavaScript files
- `/metrics` - Prometheus metrics
- `/q/health/*` - Health check endpoints

#### DNS Resolution

- `/dns-query` - DNS-over-HTTP endpoint (GET/POST)
    - This endpoint is public to allow DNS clients to use the service

#### Frontend Pages (View Only)

- `GET /ui` - Dashboard (public view)
- `GET /ui/filters` - Filter rules list (public view)
- `GET /ui/query` - DNS query tool (public access)

#### Frontend Stats Fragments (Public)

- `GET /ui/stats/queries` - Total query count
- `GET /ui/stats/cache-hits` - Cache hit count
- `GET /ui/stats/blocked` - Blocked query count
- `GET /ui/stats/threats` - Threats detected count
- `GET /ui/stats/cache-rate` - Cache hit rate
- `GET /ui/stats/filters` - Filter rules summary
- `GET /ui/filters/list` - List of filter rules (view only)

### User & Admin Endpoints (Authentication Required)

#### Frontend Pages

- `GET /ui/agent` - AI Agent page
    - **Required roles:** `admin`, `user`

- `GET /ui/admin` - Admin panel
    - **Required roles:** `admin`

#### Frontend Stats (Detailed Information)

- `GET /ui/stats/cache` - Detailed cache statistics
    - **Required roles:** `admin`, `user`

- `GET /ui/stats/security` - Security statistics
    - **Required roles:** `admin`, `user`

- `GET /ui/stats/system` - System information
    - **Required roles:** `admin`

#### Filter Management API

- `GET /api/v1/filters` - List all filters
    - **Required roles:** `admin`, `user`

- `GET /api/v1/filters/{id}` - Get specific filter
    - **Required roles:** `admin`, `user`

- `POST /api/v1/filters` - Create filter
    - **Required roles:** `admin`

- `PUT /api/v1/filters/{id}` - Update filter
    - **Required roles:** `admin`

- `DELETE /api/v1/filters/{id}` - Delete filter
    - **Required roles:** `admin`

- `PATCH /api/v1/filters/{id}/toggle` - Toggle filter
    - **Required roles:** `admin`

#### Admin API

- `GET /api/v1/admin/stats` - Overall statistics
    - **Required roles:** `admin`, `user`

- `GET /api/v1/admin/cache/stats` - Cache stats
    - **Required roles:** `admin`, `user`

- `POST /api/v1/admin/cache/clear` - Clear cache
    - **Required roles:** `admin`

- `GET /api/v1/admin/security/stats` - Security stats
    - **Required roles:** `admin`, `user`

#### AI Agent API

- `POST /api/v1/agent/analyze` - Analyze domain
    - **Required roles:** `admin`, `user`

- `POST /api/v1/agent/recommend-filters` - Get recommendations
    - **Required roles:** `admin`

- `POST /api/v1/agent/correlate-events` - Correlate events
    - **Required roles:** `admin`

## JWT Token Setup

### Generating JWT Tokens

The application uses JWT tokens for authentication. You'll need to configure JWT settings in
`application.properties`:

```properties
# JWT Configuration
mp.jwt.verify.publickey.location=META-INF/resources/publicKey.pem
mp.jwt.verify.issuer=https://your-issuer.com
quarkus.smallrye-jwt.enabled=true
```

### Creating Test Tokens

For development/testing, you can use tools like:

- https://jwt.io
- `smallrye-jwt` Maven plugin
- OpenSSL for key generation

Example JWT payload:

```json
{
  "iss": "https://your-issuer.com",
  "sub": "user@example.com",
  "groups": [
    "admin"
  ],
  "exp": 1735689600
}
```

### Making Authenticated Requests

Include the JWT token in the Authorization header:

```bash
# Admin request example
curl -X POST http://localhost:8080/api/v1/filters \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Rule","pattern":"*.test.com","type":"BLOCK","category":"custom","priority":50}'

# User request example
curl http://localhost:8080/api/v1/admin/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Frontend Authentication

The frontend needs to obtain and include JWT tokens for authenticated operations:

1. **Login Flow** - Implement a login mechanism that obtains a JWT token
2. **Token Storage** - Store the token securely (e.g., in memory, sessionStorage)
3. **Request Interceptor** - Automatically include token in AJAX/fetch requests
4. **Error Handling** - Handle 401/403 responses and redirect to login

Example JavaScript for authenticated requests:

```javascript
// Store token after login
sessionStorage.setItem('jwt_token', token);

// Include in fetch requests
async function authenticatedFetch(url, options = {}) {
    const token = sessionStorage.getItem('jwt_token');
    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };

    const response = await fetch(url, {...options, headers});

    if (response.status === 401 || response.status === 403) {
        // Redirect to login
        window.location.href = '/login';
        return;
    }

    return response;
}
```

## Development Mode

For development without authentication, you can temporarily disable security:

```properties
# application.properties (DEV ONLY)
quarkus.security.jaxrs.deny-unannotated-endpoints=false
```

**⚠️ WARNING:** Never deploy to production with security disabled!

## Security Best Practices

1. **Use HTTPS** - Always use HTTPS in production to protect JWT tokens
2. **Short Token Expiry** - Use short-lived tokens (15-60 minutes)
3. **Refresh Tokens** - Implement refresh token mechanism for long sessions
4. **Secure Storage** - Never store tokens in localStorage (vulnerable to XSS)
5. **CORS Configuration** - Restrict CORS origins in production
6. **Rate Limiting** - Implement rate limiting on authentication endpoints
7. **Audit Logging** - Log all admin operations for security auditing

## Testing Authentication

### Test as Admin

```bash
# Create a test token with admin role
# Then test admin-only endpoints
curl -X POST http://localhost:8080/api/v1/admin/cache/clear \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### Test as User

```bash
# Create a test token with user role
# Then test user-accessible endpoints
curl http://localhost:8080/api/v1/admin/stats \
  -H "Authorization: Bearer USER_TOKEN"

# Try admin-only endpoint (should fail with 403)
curl -X POST http://localhost:8080/api/v1/admin/cache/clear \
  -H "Authorization: Bearer USER_TOKEN"
```

### Test Unauthorized Access

```bash
# Try accessing admin endpoint without token (should fail with 401)
curl http://localhost:8080/ui/admin

# Try accessing protected API without token (should fail with 401)
curl http://localhost:8080/api/v1/admin/stats
```

## Troubleshooting

### 401 Unauthorized

- Token is missing or invalid
- Token has expired
- Token issuer doesn't match configuration

### 403 Forbidden

- Token is valid but user doesn't have required role
- User has "user" role but endpoint requires "admin"

### CORS Errors

- Update `quarkus.http.cors.origins` in application.properties
- Ensure preflight requests are handled properly
- Check that Authorization header is allowed

## References

- [Quarkus Security Guide](https://quarkus.io/guides/security)
- [SmallRye JWT Guide](https://quarkus.io/guides/security-jwt)
- [RFC 7519 - JSON Web Token](https://tools.ietf.org/html/rfc7519)

