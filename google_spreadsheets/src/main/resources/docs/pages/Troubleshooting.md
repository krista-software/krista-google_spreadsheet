# Troubleshooting Guide

## Overview

This guide helps you resolve common issues when using the Google Spreadsheets Extension. All error messages have been enhanced to provide clear problem identification and actionable resolution steps.

## Error Message Format

All error messages follow a consistent format:
**Problem Identification + Requirements + Resolution Steps**

This ensures you understand:
- What went wrong
- What is needed to fix it
- Specific steps to resolve the issue

## Common Issues and Solutions

### 🔐 Authentication Issues

#### "Authentication has expired and requires renewal"

**Problem**: Your Google account authorization has expired.

**Solution**:
1. Click the 'Authorize' button in the extension configuration
2. Complete the Google OAuth flow
3. Click 'Validate Attributes' to verify the connection
4. Save your configuration

**Prevention**: Monitor token expiration and re-authenticate before it expires.

#### "Account information retrieval failed due to authentication issues"

**Problem**: The extension cannot access your Google account information.

**Solution**:
1. Re-authorize your Google account
2. Ensure you have the necessary permissions
3. Verify your Google account has access to Google Sheets

### 📊 Spreadsheet Access Issues

#### "The requested spreadsheet or sheet could not be found"

**Problem**: Invalid spreadsheet ID or sheet name.

**Solution**:
1. Verify the spreadsheet ID is correct (found in the URL)
2. Check that the sheet name exists in the spreadsheet
3. Ensure the sheet name is spelled correctly (case-sensitive)
4. If the spreadsheet was recently created, wait a moment and try again

**Example**: For URL `https://docs.google.com/spreadsheets/d/1ABC123.../edit`, the ID is `1ABC123...`

#### "Access denied due to insufficient permissions"

**Problem**: You don't have the required permissions for the spreadsheet.

**Solution**:
1. Ensure you have edit permissions for the spreadsheet
2. Contact the spreadsheet owner to grant access
3. Verify you're using the correct Google account
4. Check if the spreadsheet is shared with your account

### 🌐 Network and Connectivity Issues

#### "Connection timeout occurred while trying to..."

**Problem**: Network issues or Google Sheets being temporarily slow.

**Solution**:
1. Check your internet connection
2. Wait a few minutes and try again
3. If the problem persists, check Google's service status
4. Verify Google services are not blocked by your firewall

#### "Connection was interrupted while trying to..."

**Problem**: Network instability or firewall restrictions.

**Solution**:
1. Check your internet connection stability
2. Review network settings and firewall configuration
3. Ensure Google APIs are not blocked
4. Try from a different network if possible

### ⚡ Rate Limiting and Performance

#### "Request limit exceeded due to too many operations"

**Problem**: Too many API requests sent to Google Sheets.

**Solution**:
1. Wait 1-2 minutes before trying again
2. Reduce the frequency of operations
3. Use batch operations when possible

**Note**: The extension now automatically spaces requests to prevent this issue.

### ✏️ Data Validation Issues

#### "Input validation failed because one or more required fields contain invalid data"

**Problem**: Invalid or missing required information.

**Solution**:
1. Verify all required fields are completed
2. Check that data formats are correct
3. Ensure cell references use proper notation (e.g., A1, B5, C10)
4. Validate that numeric fields contain only numbers

#### "CSV data import failed due to file format issues"

**Problem**: CSV file is not properly formatted.

**Solution**:
1. Ensure your CSV file is properly formatted
2. Check that the file contains headers and data rows
3. Verify the file is not empty
4. Ensure you have edit permissions for the target spreadsheet

### 🔧 Operation-Specific Issues

#### "Cell range clearing failed due to insufficient permissions or invalid cell range"

**Problem**: Cannot clear the specified cell range.

**Solution**:
1. Verify you have edit access to the spreadsheet
2. Check that the cell range is valid (e.g., A1:C10)
3. Ensure the range exists within the sheet boundaries

#### "Data append operation failed due to permission issues or invalid data format"

**Problem**: Cannot append data to the spreadsheet.

**Solution**:
1. Ensure you have edit permissions
2. Verify the data is in the correct format for the target cells
3. Check that the target sheet exists
4. Ensure the data types match the expected format

## Advanced Troubleshooting

### Checking Google API Status

If you're experiencing widespread issues:
1. Visit [Google Cloud Status](https://status.cloud.google.com/)
2. Check for Google Sheets API or Google Drive API incidents
3. Wait for Google to resolve service issues

### Verifying Configuration

1. **Client ID and Secret**: Ensure they're correctly configured
2. **Redirect URI**: Must match exactly with Google Cloud Console
3. **API Scopes**: Verify required scopes are enabled
4. **Project Settings**: Check Google Cloud Console project configuration

### Network Diagnostics

1. **Firewall**: Ensure Google APIs are not blocked
2. **Proxy Settings**: Configure proxy if required
3. **DNS**: Verify DNS resolution for Google services
4. **SSL/TLS**: Check certificate validation settings

## Getting Additional Help

### Documentation Resources

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth setup and management
- [Creating Google App](pages/CreatingGoogleApp.md) - Google Cloud Console setup

### Error Message Reference

All error messages are designed to be self-explanatory with:
- Clear problem description
- Specific requirements
- Step-by-step resolution instructions

### Best Practices for Error Prevention

1. **Regular Re-authentication**: Don't wait for tokens to expire
2. **Validate Inputs**: Check spreadsheet IDs and sheet names before operations
3. **Monitor Permissions**: Ensure consistent access to required spreadsheets
4. **Network Stability**: Use stable internet connections for operations
5. **Rate Limiting**: Allow appropriate delays between operations

---

**Still having issues?** Review the specific catalog request documentation for operation-specific troubleshooting steps.
