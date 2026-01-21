# Delete Sheet Columns

## Overview

The **Delete Sheet Columns** catalog request permanently removes one or more columns from a Google Sheets spreadsheet. This operation deletes the specified columns and shifts remaining columns to the left.

This request is ideal for removing unnecessary columns, restructuring spreadsheets, or cleaning up data layouts.

## Request Details

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_5968e2af-4827-42e2-8849-8980508332e1` |
| **Name** | Delete sheet columns |
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Column start index | Number | Yes | The starting column index (0-based) | Must not be null or negative | `2.0` |
| Column end index | Number | Yes | The ending column index (0-based, exclusive) | Must not be null or negative; must be > start index | `5.0` |

### Parameter Details

**Spreadsheet Id**:
- Found in the spreadsheet URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
- Must be a valid Google Sheets spreadsheet ID
- User must have edit access to the spreadsheet

**Sheet name**:
- Case-sensitive sheet name
- Must exist in the spreadsheet
- If the sheet doesn't exist, returns error: "Invalid sheet name is provided"

**Column start index** and **Column end index**:
- **Zero-based indexing**: Column A = 0, Column B = 1, Column C = 2, etc.
- **End index is exclusive**: To delete columns C-D, use start=2, end=4
- **Range calculation**: Deletes columns from start (inclusive) to end (exclusive)
- Examples:
  - Delete column C only: start=2, end=3
  - Delete columns C-E: start=2, end=5
  - Delete columns A-B: start=0, end=2

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Delete columns result | Text | Success message indicating columns were deleted | `COLUMNS deleted successfully` |

### Output Details

**Success Response**:
```
COLUMNS deleted successfully
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

3. **Column Index Validation**:
   - Column start index must not be null or negative
   - Column end index must not be null or negative
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

### Example 1: Delete Single Column

**Scenario**: Remove an unused "Notes" column (column D).

**Before**:
```
A: Name | B: Age | C: City | D: Notes | E: Status
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Employees`
- Column start index: `3.0` (Column D, 0-based)
- Column end index: `4.0` (Exclusive, so only column D is deleted)

**Result**: `COLUMNS deleted successfully`

**After**:
```
A: Name | B: Age | C: City | D: Status
```

### Example 2: Delete Multiple Columns

**Scenario**: Remove columns C, D, and E from a spreadsheet.

**Before**:
```
A: ID | B: Name | C: Temp1 | D: Temp2 | E: Temp3 | F: Status
```

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Data`
- Column start index: `2.0` (Column C)
- Column end index: `5.0` (Exclusive, deletes C, D, E)

**Result**: `COLUMNS deleted successfully`

**After**:
```
A: ID | B: Name | C: Status
```

### Example 3: Delete First Two Columns

**Scenario**: Remove the first two columns (A and B).

**Before**:
```
A: Old1 | B: Old2 | C: Name | D: Value
```

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Sheet1`
- Column start index: `0.0` (Column A)
- Column end index: `2.0` (Exclusive, deletes A and B)

**Result**: `COLUMNS deleted successfully`

**After**:
```
A: Name | B: Value
```

## Business Rules

1. **Permanent Deletion**:
   - Deleted columns cannot be recovered via API
   - Use Google Sheets undo (Ctrl+Z) immediately after deletion
   - Consider backing up data before deletion

2. **Column Shifting**:
   - Remaining columns shift left to fill the gap
   - Column references in formulas may break
   - Update any external references after deletion

3. **Zero-Based Indexing**:
   - Column A = index 0
   - Column B = index 1
   - Always use 0-based indexing

4. **Exclusive End Index**:
   - End index is not included in deletion
   - To delete 1 column: end = start + 1
   - To delete N columns: end = start + N

## Limitations

1. **Cannot Delete All Columns**:
   - Must leave at least one column in the sheet
   - Google Sheets requires minimum structure

2. **No Undo API**:
   - Cannot programmatically undo deletion
   - Must use Google Sheets UI for undo
   - Implement confirmation before deletion

3. **Formula References**:
   - Formulas referencing deleted columns will show #REF! error
   - No automatic formula update
   - Manually fix broken formulas

4. **Protected Ranges**:
   - Cannot delete columns in protected ranges
   - Remove protection before deletion
   - Check permissions first

## Best Practices

1. **Verify Column Indices**:
   - Double-check start and end indices
   - Remember: 0-based indexing
   - Test with non-critical sheets first

2. **Backup Before Deletion**:
   - Export sheet data before major deletions
   - Create a backup copy of the spreadsheet
   - Document deletion operations

3. **Check Formula Dependencies**:
   - Identify formulas referencing columns to delete
   - Update formulas before or after deletion
   - Test formulas after deletion

4. **Use Descriptive Logging**:
   - Log which columns are being deleted
   - Record timestamp and user
   - Maintain audit trail

5. **Batch Operations**:
   - Delete multiple columns in one operation
   - Minimize API calls
   - Plan deletions efficiently

## Common Use Cases

1. **Data Cleanup**:
   - Remove temporary calculation columns
   - Delete unused data fields
   - Clean up imported data

2. **Spreadsheet Restructuring**:
   - Reorganize column layout
   - Remove deprecated fields
   - Simplify data structure

3. **Template Preparation**:
   - Remove example columns
   - Clean up template sheets
   - Prepare for distribution

4. **Data Export Preparation**:
   - Remove internal-only columns
   - Delete sensitive information
   - Prepare for external sharing

## Related Catalog Requests

- [Delete Sheet Rows](pages/DeleteSheetRows.md) - Delete rows instead of columns
- [Add Empty Sheet Columns](pages/AddEmptySheetColumns.md) - Add columns
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear data without deleting structure
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve data before deletion

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `deleteSheetColumns(String spreadsheetId, String sheetName, Double columnStartIndex, Double columnEndIndex)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check spreadsheet ID and sheet name are not null/empty
   - Validate column indices are not null or negative using `ValidationHelper.isNullNegativeIndex()`
   - Throws `IllegalArgumentException` if validation fails

2. **Create ExcelSheet Object**:
   - Construct with spreadsheet ID and sheet name only

3. **Get Sheet Properties**:
   - Call `SheetsInformation.getSheetProperties()` to get sheet ID
   - Returns null if sheet doesn't exist
   - Returns list with sheet properties including sheet ID

4. **Execute Delete Operation**:
   - Call `Spreadsheet.deleteColumnsOrRowsOfSheet()` with dimension="COLUMNS"
   - Creates `DeleteDimensionRequest` with sheet ID and column range
   - Uses Google Sheets API `batchUpdate()` method

5. **API Call Details**:
   - Endpoint: `spreadsheets.batchUpdate`
   - Request: `DeleteDimensionRequest` with COLUMNS dimension
   - Uses `GoogleApiExecutor` for automatic retry

6. **Return Result**:
   - Returns "COLUMNS deleted successfully" on success

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Delete columns
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Column Range**: Start and end indices
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
3. Ensure column indices are not null or negative
4. Check end index > start index

---

### Problem: Wrong columns deleted

**Cause**: Incorrect column indices (0-based indexing confusion)

**Solution**:
1. Remember: Column A = 0, B = 1, C = 2, etc.
2. End index is exclusive
3. To delete column C only: start=2, end=3

---

### Problem: Formulas show #REF! error after deletion

**Cause**: Formulas referenced deleted columns

**Solution**:
1. Identify affected formulas before deletion
2. Update formula references
3. Consider clearing cells instead of deleting columns

---

### Problem: Cannot delete columns

**Cause**: Protected range or insufficient permissions

**Solution**:
1. Check if columns are in a protected range
2. Verify edit permissions on spreadsheet
3. Remove protection if necessary

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Delete Sheet Rows](pages/DeleteSheetRows.md) - Delete rows
- [Add Empty Sheet Columns](pages/AddEmptySheetColumns.md) - Add columns
- [Home](/) - Extension overview

