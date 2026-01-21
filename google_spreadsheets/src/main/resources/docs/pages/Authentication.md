# Authentication

## Overview

The Google Spreadsheets Extension uses OAuth 2.0 for secure authentication with Google APIs. This guide explains the authentication process, token management, and troubleshooting common authentication issues.

## Authentication Mode

### User Account Authentication

The extension uses **OAuth 2.0 User Authentication** to access Google Sheets on behalf of users.

**Key Characteristics**:
- Users authorize the extension through Google's OAuth consent screen
- Access tokens expire after 1 hour
- Refresh tokens enable automatic token renewal
- Users can revoke access at any time through Google Account settings

**When to Use**:
- Accessing user-owned spreadsheets
- Operations requiring user consent
- Interactive applications
- Development and testing environments

## OAuth 2.0 Scopes and Permissions

### Required Scopes

The extension requires the following OAuth 2.0 scopes:

| Scope | Full URL | Permission Level | Purpose |
|-------|----------|------------------|---------|
| Spreadsheets | `https://www.googleapis.com/auth/spreadsheets` | Read/Write | Full access to Google Sheets for reading, writing, and modifying spreadsheet data |
| Drive | `https://www.googleapis.com/auth/drive` | Read/Write | Access to Google Drive for creating, locating, and managing spreadsheet files |

### Why These Scopes?

**Spreadsheets Scope** (`https://www.googleapis.com/auth/spreadsheets`):
- Enables reading data from sheets
- Allows writing and updating cell values
- Permits creating and modifying sheet structure
- Required for all catalog requests

**Drive Scope** (`https://www.googleapis.com/auth/drive`):
- Enables creating new spreadsheet files
- Allows locating spreadsheets by ID
- Provides file metadata access
- Required for "Create Spreadsheet" operation

### Alternative Scopes

**Why not use read-only scopes?**
- The extension performs both read and write operations
- Read-only scopes (`spreadsheets.readonly`) would prevent data modification
- Most catalog requests require write permissions

**Why not use more restrictive Drive scopes?**
- `drive.file` scope only accesses files created by the app
- Users need to access existing spreadsheets created outside the extension
- Full `drive` scope provides necessary flexibility

## OAuth 2.0 Grant Types

### Authorization Code Grant

Used for **initial user authentication** when the user first authorizes the extension.

**Flow**:
1. User executes a catalog request
2. Extension redirects to Google OAuth consent screen
3. User signs in and grants permissions
4. Google redirects back with authorization code
5. Extension exchanges code for access token and refresh token

**Request Example**:
```http
GET https://accounts.google.com/o/oauth2/v2/auth?
  client_id=123456789-abc123.apps.googleusercontent.com&
  redirect_uri=https://your-instance.com/gsextn/callback&
  response_type=code&
  scope=https://www.googleapis.com/auth/spreadsheets%20https://www.googleapis.com/auth/drive&
  state=user@example.com&
  prompt=consent&
  access_type=offline
```

**Response Example**:
```http
HTTP/1.1 302 Found
Location: https://your-instance.com/gsextn/callback?
  code=4/0AY0e-g7X...&
  state=user@example.com
```

**Token Exchange Request**:
```http
POST https://oauth2.googleapis.com/token
Content-Type: application/x-www-form-urlencoded

code=4/0AY0e-g7X...&
client_id=123456789-abc123.apps.googleusercontent.com&
client_secret=GOCSPX-abc123xyz789&
redirect_uri=https://your-instance.com/gsextn/callback&
grant_type=authorization_code
```

**Token Exchange Response**:
```json
{
  "access_token": "ya29.a0AfH6SMBx...",
  "expires_in": 3600,
  "refresh_token": "1//0gZ9X...",
  "scope": "https://www.googleapis.com/auth/spreadsheets https://www.googleapis.com/auth/drive",
  "token_type": "Bearer"
}
```

### Refresh Token Grant

Used for **automatic token renewal** when the access token expires.

**Flow**:
1. Access token expires (after 1 hour)
2. Extension detects 401 Unauthorized response
3. Extension uses refresh token to request new access token
4. Google returns new access token
5. Extension retries original request with new token

**Request Example**:
```http
POST https://oauth2.googleapis.com/token
Content-Type: application/x-www-form-urlencoded

refresh_token=1//0gZ9X...&
client_id=123456789-abc123.apps.googleusercontent.com&
client_secret=GOCSPX-abc123xyz789&
grant_type=refresh_token
```

**Response Example**:
```json
{
  "access_token": "ya29.a0AfH6SMCy...",
  "expires_in": 3600,
  "scope": "https://www.googleapis.com/auth/spreadsheets https://www.googleapis.com/auth/drive",
  "token_type": "Bearer"
}
```

## Authentication Flow

### Complete OAuth 2.0 Flow

```
┌─────────┐                                  ┌──────────┐                    ┌────────┐
│  User   │                                  │  Krista  │                    │ Google │
└────┬────┘                                  └────┬─────┘                    └───┬────┘
     │                                            │                              │
     │ 1. Execute Catalog Request                │                              │
     ├───────────────────────────────────────────>│                              │
     │                                            │                              │
     │                                            │ 2. Check Authorization       │
     │                                            ├──────────────┐               │
     │                                            │              │               │
     │                                            │<─────────────┘               │
     │                                            │                              │
     │ 3. Redirect to Google OAuth                │                              │
     │<───────────────────────────────────────────┤                              │
     │                                            │                              │
     │ 4. Authorization Request                   │                              │
     ├────────────────────────────────────────────┼─────────────────────────────>│
     │                                            │                              │
     │ 5. Sign In & Grant Permissions             │                              │
     ├────────────────────────────────────────────┼─────────────────────────────>│
     │                                            │                              │
     │ 6. Redirect with Authorization Code        │                              │
     │<───────────────────────────────────────────┼──────────────────────────────┤
     │                                            │                              │
     │ 7. Forward Code to Callback                │                              │
     ├───────────────────────────────────────────>│                              │
     │                                            │                              │
     │                                            │ 8. Exchange Code for Tokens  │
     │                                            ├─────────────────────────────>│
     │                                            │                              │
     │                                            │ 9. Return Access & Refresh   │
     │                                            │<─────────────────────────────┤
     │                                            │                              │
     │                                            │ 10. Store Refresh Token      │
     │                                            ├──────────────┐               │
     │                                            │              │               │
     │                                            │<─────────────┘               │
     │                                            │                              │
     │ 11. Authentication Success                 │                              │
     │<───────────────────────────────────────────┤                              │
     │                                            │                              │
```

### Step-by-Step Process

**Step 1: User Executes Request**
- User executes any catalog request in Krista client
- Example: "Get Sheet Rows By Range"

**Step 2: Authorization Check**
- Extension checks if user is authorized
- If no refresh token exists, throws `MustAuthorizeException`

**Step 3: Redirect to Google**
- Extension generates OAuth authorization URL
- User is redirected to Google's consent screen

**Step 4: Authorization Request**
- User's browser sends request to Google
- Includes client ID, scopes, redirect URI, and state

**Step 5: User Grants Permissions**
- User signs in with Google account
- Reviews requested permissions
- Clicks "Allow" to grant access

**Step 6: Authorization Code Returned**
- Google redirects to callback URL
- Includes authorization code in query parameters

**Step 7: Code Forwarded to Extension**
- Callback endpoint receives authorization code
- Validates state parameter

**Step 8: Token Exchange**
- Extension exchanges authorization code for tokens
- Sends request to Google's token endpoint

**Step 9: Tokens Returned**
- Google returns access token and refresh token
- Access token valid for 1 hour
- Refresh token valid until revoked

**Step 10: Store Refresh Token**
- Extension stores refresh token securely
- Access token kept in memory only

**Step 11: Authentication Complete**
- User sees "User authenticated successfully" message
- Original request can now be executed

## Token Management

### Access Token

**Characteristics**:
- **Lifetime**: 1 hour (3600 seconds)
- **Storage**: In-memory only (not persisted)
- **Usage**: Included in every API request
- **Format**: Bearer token (e.g., `ya29.a0AfH6SMBx...`)

**Expiration Handling**:
1. Access token expires after 1 hour
2. Next API request returns 401 Unauthorized
3. Extension automatically uses refresh token
4. New access token obtained
5. Original request retried with new token

### Refresh Token

**Characteristics**:
- **Lifetime**: No expiration (until revoked)
- **Storage**: Securely persisted in extension storage
- **Usage**: Used to obtain new access tokens
- **Format**: Long-lived token (e.g., `1//0gZ9X...`)

**Important Notes**:
- Google provides only ONE refresh token per user
- If user re-authorizes, old refresh token may be invalidated
- Refresh token can be revoked by user at any time

### Token Expiration Scenarios

**Scenario 1: Access Token Expires (Normal)**
```
User Request → 401 Unauthorized → Use Refresh Token → New Access Token → Retry Request → Success
```

**Scenario 2: Refresh Token Revoked**
```
User Request → 401 Unauthorized → Use Refresh Token → 400 Invalid Grant → Require Re-authorization
```

**Scenario 3: Both Tokens Invalid**
```
User Request → No Refresh Token → Redirect to OAuth Flow → User Authorizes → Tokens Obtained → Success
```

### Token Storage

**Refresh Token Storage**:
- Stored in Krista's secure extension storage
- Encrypted at rest
- Associated with user email
- Persists across extension restarts

**Access Token Storage**:
- Kept in memory only
- Not persisted to disk
- Cleared when extension restarts
- Automatically refreshed when needed

## Security Best Practices

### Credential Security

1. **Protect Client Secret**:
   - Never expose in client-side code
   - Never commit to version control
   - Rotate periodically (every 90 days)

2. **Secure Token Storage**:
   - Refresh tokens encrypted at rest
   - Access tokens in memory only
   - No tokens in logs or error messages

3. **Use HTTPS**:
   - Always use HTTPS for redirect URIs in production
   - Prevents token interception
   - Required by Google for non-localhost URIs

### Access Control

1. **Principle of Least Privilege**:
   - Request only required scopes
   - Review permissions regularly
   - Revoke unused authorizations

2. **User Consent**:
   - Users must explicitly authorize
   - Clear explanation of permissions
   - Easy revocation process

3. **Token Revocation**:
   - Users can revoke at any time
   - Go to Google Account > Security > Third-party apps
   - Remove access for Krista application

## Troubleshooting

### Common Authentication Issues

**Problem**: "Authorization Required" when executing requests

**Cause**: User has not completed OAuth authorization

**Solution**:
1. Execute any catalog request
2. Click authorization tab that appears
3. Sign in with Google account
4. Grant requested permissions
5. Wait for success message

---

**Problem**: "Invalid Grant" error during token refresh

**Cause**: Refresh token has been revoked or expired

**Solution**:
1. User must re-authorize the extension
2. Execute a catalog request to trigger OAuth flow
3. Complete authorization again
4. New refresh token will be stored

---

**Problem**: "Redirect URI Mismatch" error

**Cause**: Redirect URI not registered in Google Console

**Solution**:
1. Copy Extension Base URL from Krista
2. Append `/gsextn/callback`
3. Add complete URI to Google Console Authorized redirect URIs
4. Ensure exact match (including protocol)

---

**Problem**: "Access Denied" errors

**Cause**: Insufficient permissions or spreadsheet access

**Solution**:
1. Verify user has edit access to spreadsheet
2. Check spreadsheet sharing settings
3. Ensure OAuth scopes include spreadsheets and drive
4. Re-authorize if scopes were changed

---

**Problem**: "Invalid Client" error

**Cause**: Incorrect Client ID or Client Secret

**Solution**:
1. Verify credentials in Google Cloud Console
2. Copy credentials again carefully
3. Update extension configuration
4. Save and test connection

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup parameters
- [Creating Google App](pages/CreatingGoogleApp.md) - Google Cloud Console setup
- [Home](/) - Extension overview

