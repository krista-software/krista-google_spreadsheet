# Creating Google App

## Overview

This guide walks you through creating a Google Cloud project and configuring OAuth 2.0 credentials required for the Google Spreadsheets Extension. You'll learn how to enable necessary APIs, configure the OAuth consent screen, and obtain Client ID and Client Secret.

## Prerequisites

- Google account (personal or workspace)
- Access to Google Cloud Console
- Administrative permissions (for workspace accounts)

## Step-by-Step Guide

### Step 1: Access Google Cloud Console

1. Open your web browser
2. Navigate to [Google Cloud Console](https://console.cloud.google.com/)
3. Sign in with your Google account

### Step 2: Create New Project

1. Click on the **Select a project** dropdown at the top of the page

![Select Project](../_media/CreatingGoogleApp_select_project.png)

2. In the popup window, click **NEW PROJECT**

![New Project](../_media/CreatingGoogleApp_new_project.png)

3. Enter project details:
   - **Project name**: Enter a descriptive name (e.g., "Krista Google Sheets Integration")
   - **Location**: Select your organization or leave as "No organization"

![Project Details](../_media/CreatingGoogleApp_project_details.png)

4. Click **CREATE**
5. Wait for the project to be created (may take a few seconds)

### Step 3: Enable Required APIs

The extension requires two Google APIs to be enabled:

#### Enable Google Sheets API

1. With your project selected, click the **Navigation menu** (☰) in the top-left
2. Navigate to **APIs & Services** > **Enabled APIs & services**

![APIs and Services](../_media/CreatingGoogleApp_apis_services.png)

3. Click **+ ENABLE APIS AND SERVICES** at the top

![Enable APIs](../_media/CreatingGoogleApp_enable_apis.png)

4. In the API Library, search for "Google Sheets API"
5. Click on **Google Sheets API** in the results
6. Click **ENABLE**

![Enable Sheets API](../_media/CreatingGoogleApp_enable_sheets.png)

#### Enable Google Drive API

1. Click **+ ENABLE APIS AND SERVICES** again
2. Search for "Google Drive API"
3. Click on **Google Drive API** in the results
4. Click **ENABLE**

![Enable Drive API](../_media/CreatingGoogleApp_enable_drive.png)

### Step 4: Configure OAuth Consent Screen

The OAuth consent screen is what users see when authorizing your application.

1. In the left sidebar, click **OAuth consent screen**

![OAuth Consent Screen Menu](../_media/CreatingGoogleApp_consent_menu.png)

2. Select **User Type**:
   - **Internal**: For Google Workspace users only (recommended for organizations)
   - **External**: For any Google account user (required for personal accounts)

![User Type](../_media/CreatingGoogleApp_user_type.png)

3. Click **CREATE**

#### App Information

Fill in the required app information:

1. **App name**: Enter a name users will see (e.g., "Krista Spreadsheets Extension")
2. **User support email**: Select your email from the dropdown
3. **App logo** (optional): Upload a logo image
4. **Application home page** (optional): Your organization's website
5. **Application privacy policy link** (optional): Link to privacy policy
6. **Application terms of service link** (optional): Link to terms of service

![OAuth Consent Screen 1](../_media/CreatingGoogleApp_consent_screen_1.png)

#### App Domain

7. **Authorized domains** (optional): Add your domain if applicable

#### Developer Contact Information

8. **Developer contact information**: Enter your email address

![OAuth Consent Screen 2](../_media/CreatingGoogleApp_consent_screen_2.png)

9. Click **SAVE AND CONTINUE**

#### Scopes

Configure the OAuth scopes your application will request:

1. Click **ADD OR REMOVE SCOPES**

![Scopes](../_media/CreatingGoogleApp_scopes.png)

2. In the scopes panel, filter or search for the following scopes:
   - `https://www.googleapis.com/auth/spreadsheets` - See, edit, create, and delete all your Google Sheets spreadsheets
   - `https://www.googleapis.com/auth/drive` - See, edit, create, and delete all of your Google Drive files

3. Select both scopes by checking their checkboxes
4. Click **UPDATE** at the bottom of the panel

![Select Scopes](../_media/CreatingGoogleApp_select_scopes.png)

5. Verify both scopes are listed in the "Your sensitive scopes" section
6. Click **SAVE AND CONTINUE**

#### Test Users (External User Type Only)

If you selected "External" user type, you need to add test users:

1. Click **+ ADD USERS**
2. Enter email addresses of users who will test the application
3. Click **ADD**

![Test Users](../_media/CreatingGoogleApp_test_users.png)

4. Click **SAVE AND CONTINUE**

#### Summary

Review the summary of your OAuth consent screen configuration:

![Summary](../_media/CreatingGoogleApp_summary.png)

Click **BACK TO DASHBOARD** to complete the consent screen setup.

### Step 5: Create OAuth 2.0 Credentials

Now create the Client ID and Client Secret:

1. In the left sidebar, click **Credentials**

![Credentials Menu](../_media/CreatingGoogleApp_credentials_menu.png)

2. Click **+ CREATE CREDENTIALS** at the top
3. Select **OAuth client ID** from the dropdown

![Create OAuth Client ID](../_media/CreatingGoogleApp_create_oauth.png)

#### Configure OAuth Client

1. **Application type**: Select **Web application**

![Application Type](../_media/CreatingGoogleApp_application_type.png)

2. **Name**: Enter a descriptive name (e.g., "Krista Extension Client")

3. **Authorized JavaScript origins** (optional): Leave empty for this extension

4. **Authorized redirect URIs**: Click **+ ADD URI**
   - Enter your redirect URI in the format: `{Extension Base URL}/gsextn/callback`
   - Example: `https://your-krista-instance.com/extensions/google-spreadsheet/gsextn/callback`
   - You can add multiple URIs for different environments (development, staging, production)

![Authorized Redirect URIs](../_media/CreatingGoogleApp_redirect_uris.png)

5. Click **CREATE**

#### Save Credentials

A popup will display your OAuth client credentials:

![Client ID and Secret](../_media/CreatingGoogleApp_credentials.png)

1. **Copy Client ID**: Click the copy icon or select and copy the Client ID
2. **Copy Client Secret**: Click the copy icon or select and copy the Client Secret
3. **Download JSON** (optional): Click **DOWNLOAD JSON** to save credentials file

⚠️ **Important**: Store these credentials securely. You'll need them for extension configuration.

4. Click **OK** to close the popup

### Step 6: Verify Configuration

Verify your setup is complete:

1. **APIs Enabled**:
   - Go to **APIs & Services** > **Enabled APIs & services**
   - Verify "Google Sheets API" and "Google Drive API" are listed

2. **OAuth Consent Screen**:
   - Go to **OAuth consent screen**
   - Verify status shows "Testing" or "In production"

3. **Credentials Created**:
   - Go to **Credentials**
   - Verify your OAuth 2.0 Client ID is listed under "OAuth 2.0 Client IDs"

## Configuration Checklist

Use this checklist to ensure all steps are completed:

- [ ] Google Cloud project created
- [ ] Google Sheets API enabled
- [ ] Google Drive API enabled
- [ ] OAuth consent screen configured
- [ ] App information provided
- [ ] Required scopes added (`spreadsheets` and `drive`)
- [ ] Test users added (if External user type)
- [ ] OAuth 2.0 Client ID created
- [ ] Application type set to "Web application"
- [ ] Redirect URI added
- [ ] Client ID copied and saved securely
- [ ] Client Secret copied and saved securely

## Security Best Practices

### Credential Protection

1. **Never Expose Credentials**:
   - Do not commit Client ID or Client Secret to version control
   - Do not share credentials via email or chat
   - Do not include credentials in screenshots or documentation

2. **Secure Storage**:
   - Store credentials in a password manager
   - Use environment variables for credentials in code
   - Encrypt credentials at rest

3. **Access Control**:
   - Limit who has access to Google Cloud Console
   - Use separate projects for development and production
   - Review project permissions regularly

### Credential Rotation

1. **Rotate Periodically**:
   - Generate new Client Secret every 90 days
   - Update extension configuration with new credentials
   - Delete old credentials after migration

2. **Rotation Process**:
   - Create new OAuth 2.0 Client ID
   - Update extension configuration
   - Test with new credentials
   - Delete old OAuth 2.0 Client ID

## Troubleshooting

### Common Issues

**Problem**: "API has not been enabled" error

**Cause**: Required APIs not enabled for the project

**Solution**:
1. Go to **APIs & Services** > **Library**
2. Search for "Google Sheets API" and "Google Drive API"
3. Click **ENABLE** for each API
4. Wait a few minutes for changes to propagate

---

**Problem**: "Redirect URI mismatch" error

**Cause**: Redirect URI not added to OAuth client configuration

**Solution**:
1. Go to **Credentials**
2. Click on your OAuth 2.0 Client ID
3. Under **Authorized redirect URIs**, click **+ ADD URI**
4. Enter exact redirect URI from Krista extension
5. Click **SAVE**

---

**Problem**: "Access blocked: This app's request is invalid" error

**Cause**: OAuth consent screen not configured properly

**Solution**:
1. Go to **OAuth consent screen**
2. Verify all required fields are filled
3. Ensure required scopes are added
4. Save changes and try again

---

**Problem**: Cannot find Client ID or Client Secret

**Cause**: Credentials not created or lost

**Solution**:
1. Go to **Credentials**
2. If OAuth client exists, click on it to view Client ID
3. Click **RESET SECRET** to generate new Client Secret
4. If no OAuth client exists, create new one following Step 5

## Next Steps

After completing Google Cloud setup:

1. **Configure Extension**: Use Client ID and Client Secret in [Extension Configuration](pages/ExtensionConfiguration.md)
2. **Complete Authentication**: Follow [Authentication](pages/Authentication.md) guide
3. **Test Connection**: Verify setup by testing connection in Krista

## Additional Resources

- [Google Cloud Console](https://console.cloud.google.com/)
- [Google Sheets API Documentation](https://developers.google.com/sheets/api)
- [Google Drive API Documentation](https://developers.google.com/drive/api)
- [OAuth 2.0 Documentation](https://developers.google.com/identity/protocols/oauth2)

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure extension with credentials
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication flow
- [Home](/) - Extension overview

