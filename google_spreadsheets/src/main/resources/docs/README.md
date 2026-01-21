# Google Spreadsheets Extension

## Overview

The Google Spreadsheets Extension enables seamless integration with Google Sheets, allowing you to perform comprehensive spreadsheet operations directly from your Krista applications. This extension provides powerful capabilities for reading, writing, modifying, and managing Google Sheets data programmatically.

## Key Features

✅ **Data Operations**
- Add data from CSV files to spreadsheets
- Add data to specific cell ranges
- Append rows to the end of existing data
- Retrieve data from specified ranges

✅ **Sheet Modification**
- Clear cell ranges
- Delete columns and rows
- Add empty columns and rows
- Create new spreadsheets

✅ **Query Operations**
- Find specific values within sheets
- Calculate sums for numeric ranges
- Retrieve sheet data by range

✅ **Advanced Capabilities**
- Automatic retry mechanism for transient failures
- User-friendly error messages
- OAuth 2.0 authentication
- Support for both user and service account authentication

## Quick Start Guide

### 1. Prerequisites

- Google Cloud Platform account
- Google Sheets API enabled
- Google Drive API enabled
- OAuth 2.0 credentials (Client ID and Client Secret)

### 2. Setup Steps

1. **Create Google Cloud Project**
   - Follow the [Creating Google App](pages/CreatingGoogleApp.md) guide

2. **Configure Extension**
   - Set up Client ID and Client Secret
   - Configure redirect URI
   - See [Extension Configuration](pages/ExtensionConfiguration.md)

3. **Authenticate**
   - Complete OAuth 2.0 authentication flow
   - See [Authentication](pages/Authentication.md)

4. **Start Using Catalog Requests**
   - Browse available operations below
   - Execute requests through Krista conversations

## Documentation Structure

### Getting Started

Essential setup and configuration documentation:

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup parameters and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication and token management
- [Creating Google App](pages/CreatingGoogleApp.md) - Google Cloud Console setup guide
- [Troubleshooting](pages/Troubleshooting.md) - Comprehensive error resolution guide

### Data Operations

Catalog requests for adding and managing data in spreadsheets:

- [Add Data Rows From CSV](pages/AddDataRowsFromCsv.md) - Import CSV data into sheets
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data to specific cell ranges
- [Append Row Data At The End Of Spreadsheet](pages/AppendRowDataAtTheEndOfSpreadsheet.md) - Append rows to existing data

### Sheet Modification

Catalog requests for modifying sheet structure:

- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear data from cell ranges
- [Delete Sheet Columns](pages/DeleteSheetColumns.md) - Remove columns from sheets
- [Delete Sheet Rows](pages/DeleteSheetRows.md) - Remove rows from sheets
- [Add Empty Sheet Columns](pages/AddEmptySheetColumns.md) - Insert empty columns
- [Add Empty Sheet Rows](pages/AddEmptySheetRows.md) - Insert empty rows

### Query Operations

Catalog requests for retrieving and analyzing data:

- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve data from specified ranges
- [Find Values In Sheet](pages/FindValuesInSheet.md) - Search for specific values
- [Get Sum By Sheet Range](pages/GetSumBySheetRange.md) - Calculate sum of numeric values

### Spreadsheet Management

Catalog requests for creating and managing spreadsheets:

- [Create Spreadsheet](pages/CreateSpreadsheet.md) - Create new spreadsheet files

## Support & Resources

### Version Information

- **Extension Version**: 2.0.4
- **Java Version**: 21
- **Google Sheets API**: v4
- **Google Drive API**: v3

### API Scopes

This extension requires the following Google API scopes:

- `https://www.googleapis.com/auth/spreadsheets` - Full access to Google Sheets
- `https://www.googleapis.com/auth/drive` - Access to Google Drive

### Enhanced Error Handling

The extension includes comprehensive error handling with significant improvements:

- **Automatic Retry with Rate Limiting**: Transient failures are automatically retried (up to 3 attempts) with intelligent spacing to prevent API quota exhaustion
- **Enhanced User-Friendly Messages**: Clear, actionable error messages following the format: Problem + Requirements + Resolution Steps
- **Detailed Logging**: Technical details preserved for debugging while users see friendly messages
- **Smart Error Detection**: Distinguishes between retriable and non-retriable errors
- **Context-Aware Guidance**: Error messages include specific context and next steps

### Enhanced Error Messages

| Error Category | Enhanced Message Example | Key Improvements |
|----------------|--------------------------|------------------|
| **Authentication** | "Authentication has expired and requires renewal. Please click 'Authorize' to reconnect your Google account and restore access to your spreadsheets." | Clear action steps, specific button references |
| **Permissions** | "Access denied due to insufficient permissions. Please ensure you have edit permissions for this spreadsheet or contact the spreadsheet owner to grant access." | Explains permission types, suggests contacting owner |
| **Not Found** | "The requested spreadsheet or sheet could not be found. Please verify the spreadsheet ID is correct and the sheet name exists. If the spreadsheet was recently created, please wait a moment and try again." | Covers timing issues, provides verification steps |
| **Network Issues** | "Connection timeout occurred while trying to access the spreadsheet. This may be due to network issues or Google Sheets being temporarily slow. Please check your internet connection and try again. If the problem persists, wait a few minutes before retrying." | Explains possible causes, provides escalation steps |
| **Rate Limiting** | "Request limit exceeded due to too many operations. Google Sheets has temporarily restricted access. Please wait 1-2 minutes before trying again." | Explains Google's restrictions, gives specific wait time |
| **Validation** | "Input validation failed because one or more required fields contain invalid data. Please verify that all required fields are filled with valid values and try again." | Identifies specific problem, explains requirements |

### Troubleshooting Guide

#### Common Issues and Solutions

**🔐 Authentication Problems**
- **Issue**: "Authentication has expired and requires renewal"
- **Solution**: Click 'Authorize' button to reconnect your Google account
- **Prevention**: Monitor token expiration and re-authenticate proactively

**📊 Spreadsheet Access Issues**
- **Issue**: "The requested spreadsheet or sheet could not be found"
- **Solution**: Verify spreadsheet ID and sheet name are correct
- **Note**: If spreadsheet was recently created, wait a moment and retry

**🌐 Network Connectivity**
- **Issue**: "Connection timeout occurred while trying to..."
- **Solution**: Check internet connection, wait a few minutes if Google Sheets is slow
- **Advanced**: Check firewall settings for Google API access

**⚡ Rate Limiting**
- **Issue**: "Request limit exceeded due to too many operations"
- **Solution**: Wait 1-2 minutes before retrying
- **Prevention**: The extension now automatically spaces requests to prevent this

**✏️ Permission Errors**
- **Issue**: "Access denied due to insufficient permissions"
- **Solution**: Ensure you have edit permissions or contact spreadsheet owner
- **Check**: Verify you're using the correct Google account

#### Getting Additional Help

- See the comprehensive [Troubleshooting Guide](pages/Troubleshooting.md) for detailed solutions
- Review individual catalog request documentation for operation-specific issues
- Verify your [configuration](pages/ExtensionConfiguration.md) is correct
- Check the [Authentication](pages/Authentication.md) guide for setup issues

## Best Practices

### 1. Authentication Management
- Store credentials securely
- Monitor token expiration
- Re-authenticate proactively

### 2. Data Operations
- Validate cell ranges before operations
- Use batch operations when possible
- Handle large datasets in chunks

### 3. Error Handling
- Implement retry logic for critical operations
- Log errors for debugging
- Provide user-friendly error messages

### 4. Performance Optimization
- Minimize API calls
- Use appropriate cell ranges
- Cache frequently accessed data

## Security Considerations

⚠️ **Important Security Notes**:

1. **Credential Protection**
   - Never expose Client ID or Client Secret in code
   - Use secure storage for OAuth tokens
   - Rotate credentials periodically

2. **Access Control**
   - Grant minimum required permissions
   - Use service accounts for automated processes
   - Review OAuth scopes regularly

3. **Data Privacy**
   - Comply with data protection regulations
   - Encrypt sensitive data
   - Audit access logs

## Release Notes

For detailed release notes and version history, see the [Release Notes](pages/release_notes.md).

## Additional Resources

- [Google Sheets API Documentation](https://developers.google.com/sheets/api)
- [Google Drive API Documentation](https://developers.google.com/drive/api)
- [OAuth 2.0 Documentation](https://developers.google.com/identity/protocols/oauth2)

---

**Need Help?** Start with the [Quick Start Guide](#quick-start-guide) or browse the documentation by category above.

