# Create Spreadsheet

## Overview

The **Create Spreadsheet** catalog request creates a new Google Sheets spreadsheet in the user's Google Drive. The spreadsheet is created at the root location of Google Drive with a default empty sheet.

This request is ideal for programmatically creating new spreadsheets, generating reports, or initializing data storage.

## Request Details

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_55d3cdef-8e9c-44a0-98bd-eba78771613b` |
| **Name** | Create spreadsheet |
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet name | Text | Yes | The name for the new spreadsheet | Must not be null or empty | `Monthly Sales Report` |

### Parameter Details

**Spreadsheet name**:
- The display name for the new spreadsheet
- Must not be null, empty, or blank
- Can contain spaces and special characters
- Does not need to be unique (Google Drive allows duplicate names)
- Maximum length: 400 characters (Google Drive limit)
- Examples: "Sales Report 2024", "Employee Data", "Q1 Analysis"

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Drive file | Entity(Drive File) | Entity containing spreadsheet details | See below |

### Output Details

**Success Response**:
Returns a `DriveFile` entity with the following fields:

| Field Name | Type | Description | Example |
|-----------|------|-------------|---------|
| File name | Text | The name of the created spreadsheet | `Monthly Sales Report` |
| File Id | Text | The unique identifier of the spreadsheet | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| File path | Text | The path in Google Drive (null for root) | `null` |
| Mime type | Text | The MIME type (null in response) | `null` |
| Modified time | Text | Last modified timestamp (null in response) | `null` |

**Example Response**:
```json
{
  "File name": "Monthly Sales Report",
  "File Id": "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
  "File path": null,
  "Mime type": null,
  "Modified time": null
}
```

**Note**: The File Id can be used in subsequent operations to reference the spreadsheet.

## Validation Rules

The following validation rules are enforced:

1. **Spreadsheet Name Validation**:
   - Must not be null
   - Must not be empty or blank
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

2. **Authentication**:
   - User must be authenticated with Google
   - Must have valid OAuth tokens
   - Must have permission to create files in Google Drive

## Error Handling

### Error Types

**INPUT_ERROR** - Invalid input parameters:
```
You have provided Invalid inputs.
```

**SYSTEM_ERROR** - Google API errors with user-friendly messages:

| HTTP Status | User-Friendly Message |
|-------------|----------------------|
| 401 | Authentication failed. Please re-authorize your Google account. |
| 403 | Access denied. You don't have permission to create spreadsheets in Google Drive. |
| 429 | Too many requests. Please wait a moment and try again. |
| 500/502/503/504 | Google Sheets service is temporarily unavailable. Please try again in a few moments. |
| Timeout | Request timed out. Please check your internet connection and try again. |

### Retry Behavior

This request includes automatic retry for transient failures:

- **Maximum Attempts**: 3
- **Retriable Errors**: Connection timeouts, HTTP 429, 500, 502, 503, 504
- **Non-Retriable Errors**: HTTP 400, 401, 403, 404
- **Backoff Strategy**: Exponential with jitter (1s, 2s, 4s)

## Usage Examples

### Example 1: Create Monthly Report

**Scenario**: Create a new spreadsheet for monthly sales reporting.

**Request Parameters**:
- Spreadsheet name: `Sales Report - January 2024`

**Result**:
```json
{
  "File name": "Sales Report - January 2024",
  "File Id": "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
}
```

**Next Steps**:
- Use the File Id to add data to the spreadsheet
- Share the spreadsheet with team members
- Add sheets for different data categories

### Example 2: Create Employee Database

**Scenario**: Create a new spreadsheet to track employee information.

**Request Parameters**:
- Spreadsheet name: `Employee Database 2024`

**Result**:
```json
{
  "File name": "Employee Database 2024",
  "File Id": "1AbCdEfGhIjKlMnOpQrStUvWxYz123456789"
}
```

### Example 3: Create Inventory Tracker

**Scenario**: Create a spreadsheet for inventory management.

**Request Parameters**:
- Spreadsheet name: `Warehouse Inventory - Q1`

**Result**:
```json
{
  "File name": "Warehouse Inventory - Q1",
  "File Id": "1XyZ987654321AbCdEfGhIjKlMnOpQrStUv"
}
```

## Business Rules

1. **Default Structure**:
   - New spreadsheet contains one default sheet named "Sheet1"
   - Sheet is empty with no data
   - Default size: 1000 rows × 26 columns (A-Z)

2. **Location**:
   - Created at root of Google Drive
   - Not placed in any specific folder
   - Can be moved to folders after creation using Google Drive API

3. **Permissions**:
   - Creator has full ownership
   - Not shared with anyone by default
   - Can be shared after creation

4. **Naming**:
   - Duplicate names are allowed
   - Name can be changed after creation
   - Name is displayed in Google Drive

## Limitations

1. **No Folder Specification**:
   - Cannot specify target folder in this request
   - Always created at root level
   - Use Google Drive API to move after creation

2. **No Initial Data**:
   - Spreadsheet is created empty
   - Use other catalog requests to add data
   - Cannot populate during creation

3. **No Template Support**:
   - Cannot create from template
   - Always creates blank spreadsheet
   - Must configure structure after creation

4. **Storage Quota**:
   - Counts against Google Drive storage quota
   - Free accounts: 15 GB limit
   - Check available storage before creating

## Best Practices

1. **Use Descriptive Names**:
   - Include date or version in name
   - Use clear, meaningful names
   - Follow naming conventions

2. **Track File IDs**:
   - Store the returned File Id
   - Use for subsequent operations
   - Document spreadsheet purposes

3. **Organize After Creation**:
   - Move to appropriate folders
   - Set up sharing permissions
   - Add initial structure (headers, sheets)

4. **Error Handling**:
   - Check for successful creation
   - Handle authentication errors
   - Implement retry logic

5. **Cleanup**:
   - Delete unused spreadsheets
   - Monitor storage usage
   - Archive old spreadsheets

## Common Use Cases

1. **Report Generation**:
   - Create monthly/quarterly reports
   - Generate analysis spreadsheets
   - Build dashboard data sources

2. **Data Collection**:
   - Create forms for data entry
   - Build survey response sheets
   - Initialize data tracking

3. **Project Management**:
   - Create project tracking sheets
   - Build task lists
   - Generate status reports

4. **Automation**:
   - Programmatically create spreadsheets
   - Generate reports on schedule
   - Initialize data pipelines

## Related Catalog Requests

- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data to created spreadsheet
- [Append Row Data At The End Of Spreadsheet](pages/AppendRowDataAtTheEndOfSpreadsheet.md) - Add rows to spreadsheet
- [Add Data Rows From CSV](pages/AddDataRowsFromCsv.md) - Import CSV data
- [Add Empty Sheet Rows](pages/AddEmptySheetRows.md) - Add structure to spreadsheet

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `createSpreadsheet(String spreadsheetName)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check spreadsheet name is not null/empty using `ValidationHelper.isNullEmptyString()`
   - Throws `IllegalArgumentException` if validation fails with message "You have provided Invalid inputs."

2. **Create Spreadsheet Object**:
   - Create `Spreadsheet` object with `SpreadsheetProperties`
   - Set title to provided spreadsheet name
   - No other properties configured

3. **Execute Create Operation**:
   - Call `Spreadsheet.createSpreadSheetFile()` with retry logic
   - Uses Google Sheets API `spreadsheets().create()` method
   - Requests only spreadsheetId field in response

4. **API Call Details**:
   - Endpoint: `spreadsheets.create`
   - Request body: Spreadsheet object with title
   - Fields requested: `spreadsheetId`
   - Uses `GoogleApiExecutor` for automatic retry

5. **Create DriveFile Entity**:
   - Construct `DriveFile` entity with:
     - fileName: spreadsheet name
     - fileId: returned spreadsheet ID
   - Other fields (filePath, mimeType, modifiedTime) are null

6. **Return Result**:
   - Returns `DriveFile` entity
   - Entity can be used in workflows

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Create spreadsheet
- **Spreadsheet Name**: Logged for traceability
- **Spreadsheet ID**: Logged after creation
- **Result**: Success or failure

## Troubleshooting

### Problem: "You have provided Invalid inputs"

**Cause**: Spreadsheet name is null, empty, or blank

**Solution**:
1. Verify spreadsheet name is not null
2. Ensure name is not empty string
3. Check for whitespace-only names

---

### Problem: "Authentication failed"

**Cause**: OAuth tokens are invalid or expired

**Solution**:
1. Re-authorize the Google account
2. Check OAuth configuration
3. Verify refresh token is valid

---

### Problem: "Access denied"

**Cause**: Insufficient permissions to create files

**Solution**:
1. Verify OAuth scopes include Google Drive access
2. Check user has permission to create files
3. Ensure account is not restricted

---

### Problem: Spreadsheet created but not visible

**Cause**: Created at root level, may be hard to find

**Solution**:
1. Search Google Drive by name
2. Check "My Drive" root folder
3. Use File Id to access directly

---

### Problem: Storage quota exceeded

**Cause**: Google Drive storage limit reached

**Solution**:
1. Check available storage
2. Delete unused files
3. Upgrade storage plan if needed

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data to spreadsheet
- [Append Row Data At The End Of Spreadsheet](pages/AppendRowDataAtTheEndOfSpreadsheet.md) - Add rows
- [Home](/) - Extension overview

