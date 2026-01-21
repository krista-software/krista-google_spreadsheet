# Delete Sheet Rows

## Overview

The **Delete Sheet Rows** catalog request permanently removes one or more rows from a Google Sheets spreadsheet. This operation deletes the specified rows and shifts remaining rows upward.

This request is ideal for removing unnecessary rows, cleaning up data, or restructuring spreadsheets.

## Request Details

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_77f03a68-1e11-40bd-aafa-5040e4bef730` |
| **Name** | Delete sheet rows |
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Row start index | Number | Yes | The starting row index (0-based) | Must not be null or negative | `5.0` |
| Row end index | Number | Yes | The ending row index (0-based, exclusive) | Must not be null or negative; must be > start index | `10.0` |

### Parameter Details

**Spreadsheet Id**:
- Found in the spreadsheet URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
- Must be a valid Google Sheets spreadsheet ID
- User must have edit access to the spreadsheet

**Sheet name**:
- Case-sensitive sheet name
- Must exist in the spreadsheet
- If the sheet doesn't exist, returns error: "Invalid sheet name is provided"

**Row start index** and **Row end index**:
- **Zero-based indexing**: Row 1 = 0, Row 2 = 1, Row 3 = 2, etc.
- **End index is exclusive**: To delete rows 6-10, use start=5, end=10
- **Range calculation**: Deletes rows from start (inclusive) to end (exclusive)
- Examples:
  - Delete row 3 only: start=2, end=3
  - Delete rows 5-10: start=4, end=10
  - Delete rows 1-2: start=0, end=2

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Delete rows result | Text | Success message indicating rows were deleted | `ROWS deleted successfully` |

### Output Details

**Success Response**:
```
ROWS deleted successfully
```

**Failure Response**:
```
Invalid sheet name is provided
```

## Validation Rules

The following validation rules are enforced:

1. **Spreadsheet ID Validation**:
   - Must not be null or empty
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

2. **Sheet Name Validation**:
   - Must not be null or empty
   - Must exist in the spreadsheet
   - Violations throw: `IllegalArgumentException` with message "You have provided Invalid inputs."
   - Non-existent sheet returns: "Invalid sheet name is provided"

3. **Row Index Validation**:
   - Row start index must not be null or negative
   - Row end index must not be null or negative
   - End index must be greater than start index
   - Violations throw: `IllegalArgumentException` with message "You have provided Invalid inputs."

4. **Spreadsheet Access**:
   - User must have edit permissions
   - If access denied, returns appropriate error message

## Error Handling

### Error Types

**INPUT_ERROR** - Invalid input parameters:
```
You have provided Invalid inputs.
```

**LOGIC_ERROR** - Business logic errors:
```
Invalid sheet name is provided
```

**SYSTEM_ERROR** - Google API errors with user-friendly messages:

| HTTP Status | User-Friendly Message |
|-------------|----------------------|
| 404 | Spreadsheet or sheet not found. Please verify the spreadsheet ID and sheet name are correct. |
| 401 | Authentication failed. Please re-authorize your Google account. |
| 403 | Access denied. You don't have permission to edit this spreadsheet. |
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

### Example 1: Delete Single Row

**Scenario**: Remove a duplicate entry (row 5).

**Before**:
```
Row 1: Name | Age | City
Row 2: Alice | 30 | NYC
Row 3: Bob | 25 | LA
Row 4: Charlie | 35 | Chicago
Row 5: Bob | 25 | LA (duplicate)
Row 6: Diana | 28 | Boston
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Employees`
- Row start index: `4.0` (Row 5, 0-based)
- Row end index: `5.0` (Exclusive, so only row 5 is deleted)

**Result**: `ROWS deleted successfully`

**After**:
```
Row 1: Name | Age | City
Row 2: Alice | 30 | NYC
Row 3: Bob | 25 | LA
Row 4: Charlie | 35 | Chicago
Row 5: Diana | 28 | Boston
```

### Example 2: Delete Multiple Rows

**Scenario**: Remove rows 10-15 containing old data.

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Sales Data`
- Row start index: `9.0` (Row 10, 0-based)
- Row end index: `15.0` (Exclusive, deletes rows 10-15)

**Result**: `ROWS deleted successfully`

### Example 3: Delete Header Row

**Scenario**: Remove the first row (header).

**Before**:
```
Row 1: Old Header | Old Header 2
Row 2: Data 1 | Data 2
Row 3: Data 3 | Data 4
```

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Sheet1`
- Row start index: `0.0` (Row 1)
- Row end index: `1.0` (Exclusive, deletes only row 1)

**Result**: `ROWS deleted successfully`

**After**:
```
Row 1: Data 1 | Data 2
Row 2: Data 3 | Data 4
```

## Business Rules

1. **Permanent Deletion**:
   - Deleted rows cannot be recovered via API
   - Use Google Sheets undo (Ctrl+Z) immediately after deletion
   - Consider backing up data before deletion

2. **Row Shifting**:
   - Remaining rows shift up to fill the gap
   - Row numbers change after deletion
   - Update any row-based references

3. **Zero-Based Indexing**:
   - Row 1 = index 0
   - Row 2 = index 1
   - Always use 0-based indexing

4. **Exclusive End Index**:
   - End index is not included in deletion
   - To delete 1 row: end = start + 1
   - To delete N rows: end = start + N

## Limitations

1. **Cannot Delete All Rows**:
   - Must leave at least one row in the sheet
   - Google Sheets requires minimum structure

2. **No Undo API**:
   - Cannot programmatically undo deletion
   - Must use Google Sheets UI for undo
   - Implement confirmation before deletion

3. **Formula References**:
   - Formulas referencing deleted rows may break
   - Row references in formulas update automatically
   - Test formulas after deletion

4. **Protected Ranges**:
   - Cannot delete rows in protected ranges
   - Remove protection before deletion
   - Check permissions first

## Best Practices

1. **Verify Row Indices**:
   - Double-check start and end indices
   - Remember: 0-based indexing
   - Test with non-critical sheets first

2. **Backup Before Deletion**:
   - Export sheet data before major deletions
   - Create a backup copy of the spreadsheet
   - Document deletion operations

3. **Preserve Headers**:
   - Avoid deleting header rows (row 1)
   - Start deletion from row 2 or later
   - Keep column headers intact

4. **Check Dependencies**:
   - Identify formulas referencing rows to delete
   - Update references before or after deletion
   - Test dependent calculations

5. **Batch Operations**:
   - Delete multiple rows in one operation
   - Minimize API calls
   - Plan deletions efficiently

## Common Use Cases

1. **Data Cleanup**:
   - Remove duplicate entries
   - Delete invalid or test data
   - Clean up imported data

2. **Record Removal**:
   - Delete obsolete records
   - Remove archived data
   - Clean up historical entries

3. **Spreadsheet Maintenance**:
   - Remove empty rows
   - Delete temporary data
   - Clean up calculation rows

4. **Data Filtering**:
   - Remove rows not meeting criteria
   - Delete filtered-out records
   - Clean up after data processing

## Related Catalog Requests

- [Delete Sheet Columns](pages/DeleteSheetColumns.md) - Delete columns instead of rows
- [Add Empty Sheet Rows](pages/AddEmptySheetRows.md) - Add rows
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear data without deleting structure
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve data before deletion

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `deleteSheetRows(String spreadsheetId, String sheetName, Double rowStartIndex, Double rowEndIndex)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check spreadsheet ID and sheet name are not null/empty
   - Validate row indices are not null or negative using `ValidationHelper.isNullNegativeIndex()`
   - Throws `IllegalArgumentException` if validation fails

2. **Create ExcelSheet Object**:
   - Construct with spreadsheet ID and sheet name only

3. **Get Sheet Properties**:
   - Call `SheetsInformation.getSheetProperties()` to get sheet ID
   - Returns null if sheet doesn't exist
   - Returns list with sheet properties including sheet ID

4. **Execute Delete Operation**:
   - Call `Spreadsheet.deleteColumnsOrRowsOfSheet()` with dimension="ROWS"
   - Creates `DeleteDimensionRequest` with sheet ID and row range
   - Uses Google Sheets API `batchUpdate()` method

5. **API Call Details**:
   - Endpoint: `spreadsheets.batchUpdate`
   - Request: `DeleteDimensionRequest` with ROWS dimension
   - Uses `GoogleApiExecutor` for automatic retry

6. **Return Result**:
   - Returns "ROWS deleted successfully" on success

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Delete rows
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Row Range**: Start and end indices
- **Result**: Success or failure

## Troubleshooting

### Problem: "Invalid sheet name is provided"

**Cause**: Sheet name doesn't exist in the spreadsheet

**Solution**:
1. Check sheet name spelling (case-sensitive)
2. Verify sheet exists in the spreadsheet
3. List all sheets to confirm name

---

### Problem: "You have provided Invalid inputs"

**Cause**: One or more parameters are null, empty, or negative

**Solution**:
1. Verify spreadsheet ID is not empty
2. Verify sheet name is not empty
3. Ensure row indices are not null or negative
4. Check end index > start index

---

### Problem: Wrong rows deleted

**Cause**: Incorrect row indices (0-based indexing confusion)

**Solution**:
1. Remember: Row 1 = 0, Row 2 = 1, Row 3 = 2, etc.
2. End index is exclusive
3. To delete row 5 only: start=4, end=5

---

### Problem: Header row accidentally deleted

**Cause**: Started deletion from index 0

**Solution**:
1. Always preserve header row (index 0)
2. Start deletion from index 1 or later
3. Use undo (Ctrl+Z) if deleted accidentally

---

### Problem: Cannot delete rows

**Cause**: Protected range or insufficient permissions

**Solution**:
1. Check if rows are in a protected range
2. Verify edit permissions on spreadsheet
3. Remove protection if necessary

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Delete Sheet Columns](pages/DeleteSheetColumns.md) - Delete columns
- [Add Empty Sheet Rows](pages/AddEmptySheetRows.md) - Add rows
- [Home](/) - Extension overview

