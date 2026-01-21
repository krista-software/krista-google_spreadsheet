# Append Row Data At The End Of Spreadsheet

## Overview

The **Append Row Data At The End Of Spreadsheet** catalog request adds new rows of data to the end of existing data in a Google Sheets spreadsheet. This operation automatically finds the next available row after the last row containing data and appends the new rows there.

This request is ideal for adding new records to existing datasets, logging data over time, or building cumulative reports without overwriting existing information.

## Request Details

| Property | Value |
|----------|-------|
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | ✅ Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet where data will be appended | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Cell data | [ [ Text ] ] | Yes | Two-dimensional array of data to append (rows and columns) | Must not be null or empty | `[["John", "Doe", "30"], ["Jane", "Smith", "25"]]` |

### Parameter Details

**Spreadsheet Id**:
- Found in the spreadsheet URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
- Must be a valid Google Sheets spreadsheet ID
- User must have edit access to the spreadsheet

**Sheet name**:
- Case-sensitive sheet name
- Must exist in the spreadsheet
- If the sheet doesn't exist, returns error: "Invalid sheet name is provided"

**Cell data**:
- Two-dimensional array structure: `[[row1_col1, row1_col2], [row2_col1, row2_col2]]`
- Each inner array represents one row
- All rows should have the same number of columns for consistent formatting
- Empty strings allowed for blank cells

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Append cells result | Number | The total number of cells that were updated | `6.0` |

### Output Details

**Success Response**:
Returns the count of cells updated as a number.

**Example**:
- Appending 2 rows with 3 columns each = 6 cells updated
- Output: `6.0`

## Validation Rules

The following validation rules are enforced:

1. **Spreadsheet ID Validation**:
   - Must not be null
   - Must not be empty or blank
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

2. **Sheet Name Validation**:
   - Must not be null
   - Must not be empty or blank
   - Must exist in the spreadsheet
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."
   - Non-existent sheet returns: "Invalid sheet name is provided"

3. **Cell Data Validation**:
   - Must not be null
   - Must not be empty (at least one row required)
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

4. **Spreadsheet Access**:
   - User must have edit permissions
   - If access denied, returns appropriate error message

## Error Handling

### Error Types

**INPUT_ERROR** - Invalid input parameters:
```
Input validation failed because one or more required fields contain invalid data. Please verify that all required fields are filled with valid values and try again.
```

**LOGIC_ERROR** - Business logic errors:
```
Sheet operation failed because the specified sheet name does not exist in the spreadsheet. Please verify the sheet name is correct and exists in your spreadsheet.
```

**SYSTEM_ERROR** - Google API errors with enhanced user-friendly messages:

| HTTP Status | Enhanced User-Friendly Message |
|-------------|--------------------------------|
| 404 | The requested spreadsheet or sheet could not be found. Please verify the spreadsheet ID is correct and the sheet name exists. If the spreadsheet was recently created, please wait a moment and try again. |
| 401 | Authentication has expired and requires renewal. Please click 'Authorize' to reconnect your Google account and restore access to your spreadsheets. |
| 403 | Access denied due to insufficient permissions. Please ensure you have edit permissions for this spreadsheet or contact the spreadsheet owner to grant access. |
| 429 | Request limit exceeded due to too many operations. Google Sheets has temporarily restricted access. Please wait 1-2 minutes before trying again. |
| 500/502/503/504 | Google Sheets service is temporarily unavailable due to maintenance or high load. Please wait a few minutes and try again. If the issue persists, check Google's service status. |
| Timeout | Connection timeout occurred while trying to append data to the spreadsheet. This may be due to network issues or Google Sheets being temporarily slow. Please check your internet connection and try again. If the problem persists, wait a few minutes before retrying. |

### Retry Behavior

This request includes automatic retry for transient failures:

- **Maximum Attempts**: 3
- **Retriable Errors**: Connection timeouts, HTTP 429, 500, 502, 503, 504
- **Non-Retriable Errors**: HTTP 400, 401, 403, 404
- **Backoff Strategy**: Exponential with jitter (1s, 2s, 4s)

Example retry log:
```
WARN: Google API request failed (attempt 1/3) - Connection timeout. Retrying in 1000ms - Context: Append cells to spreadsheet ABC123, sheet: Sheet1
```

## Usage Examples

### Example 1: Append Daily Sales Records

**Scenario**: Add today's sales transactions to an existing sales log.

**Existing Data in Sheet**:
```
Date       | Product  | Quantity | Revenue
2024-01-01 | Widget A | 10       | 100.00
2024-01-02 | Widget B | 5        | 75.00
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Sales`
- Cell data: `[["2024-01-03", "Widget A", "8", "80.00"], ["2024-01-03", "Widget C", "12", "120.00"]]`

**Result**:
```
8.0
```

**Resulting Sheet**:
```
Date       | Product  | Quantity | Revenue
2024-01-01 | Widget A | 10       | 100.00
2024-01-02 | Widget B | 5        | 75.00
2024-01-03 | Widget A | 8        | 80.00
2024-01-03 | Widget C | 12       | 120.00
```

### Example 2: Log System Events

**Scenario**: Append new system event logs to a monitoring spreadsheet.

**Existing Data in Sheet**:
```
Timestamp           | Event Type | Severity | Message
2024-01-15 10:00:00 | Login      | INFO     | User logged in
2024-01-15 10:05:00 | Error      | ERROR    | Connection failed
```

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Event Log`
- Cell data: `[["2024-01-15 10:10:00", "Warning", "WARN", "High memory usage"]]`

**Result**:
```
4.0
```

### Example 3: Add Survey Responses

**Scenario**: Append new survey responses as they are submitted.

**Existing Data in Sheet**:
```
Respondent | Q1 Rating | Q2 Rating | Comments
Alice      | 5         | 4         | Great service
Bob        | 4         | 5         | Very helpful
```

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Survey Responses`
- Cell data: `[["Charlie", "5", "5", "Excellent experience"], ["Diana", "3", "4", "Good overall"]]`

**Result**:
```
8.0
```

## Business Rules

1. **Automatic Row Detection**:
   - Extension automatically finds the next available row
   - No need to specify row numbers
   - Appends immediately after the last row with data

2. **Column Alignment**:
   - New rows should match the column count of existing data
   - Mismatched column counts are allowed but may cause formatting issues
   - Consider the existing data structure when appending

3. **Data Preservation**:
   - Existing data is never overwritten
   - New rows are always added at the end
   - Safe for concurrent append operations

4. **Empty Rows**:
   - Empty rows between data are ignored
   - Appends after the last row containing any data
   - Maintains data continuity

## Limitations

1. **No Row Specification**:
   - Cannot specify which row to append to
   - Always appends to the end
   - Use "Add Data To Sheet Cells" for specific row placement

2. **Sheet Size Limits**:
   - Google Sheets maximum: 10 million cells per spreadsheet
   - Maximum rows per sheet: 40,000 (for performance)
   - Consider archiving old data if approaching limits

3. **Column Count**:
   - No automatic column matching
   - Responsibility of caller to match existing column structure
   - Mismatched columns may cause data misalignment

4. **Concurrent Appends**:
   - Multiple simultaneous appends may interleave
   - Consider using locking mechanisms for critical data
   - Google Sheets handles concurrent writes but order not guaranteed

## Best Practices

1. **Maintain Consistent Structure**:
   - Match the number of columns in existing data
   - Use same data types for each column
   - Include headers in the first row for clarity

2. **Validate Data Before Appending**:
   - Check data format and types
   - Ensure required fields are populated
   - Validate data ranges and constraints

3. **Handle Large Batches**:
   - For >1000 rows, consider batching into smaller chunks
   - Monitor API quota usage
   - Implement rate limiting if needed

4. **Error Recovery**:
   - Log append operations for audit trail
   - Implement retry logic for transient failures
   - Verify append success before marking data as processed

5. **Performance Optimization**:
   - Batch multiple rows in single append operation
   - Minimize number of API calls
   - Use appropriate data structures

## Common Use Cases

1. **Data Logging**:
   - Application logs
   - System events
   - User activity tracking
   - Performance metrics

2. **Form Submissions**:
   - Survey responses
   - Registration forms
   - Feedback collection
   - Order submissions

3. **Time-Series Data**:
   - Daily sales records
   - Sensor readings
   - Stock prices
   - Weather data

4. **Incremental Reporting**:
   - Adding new transactions
   - Updating inventory records
   - Appending customer records
   - Building cumulative datasets

## Related Catalog Requests

- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data to specific cell range
- [Add Data Rows From CSV](pages/AddDataRowsFromCsv.md) - Import CSV data
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve existing data
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear data before appending

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `appendCellsOfSheet(String spreadsheetId, String sheetName, List<List<String>> cellData)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check spreadsheet ID and sheet name are not null/empty
   - Throws `IllegalArgumentException` if validation fails

2. **Create ExcelSheet Object**:
   - Construct ExcelSheet with spreadsheet ID and sheet name only
   - No cell range specified (append mode)

3. **Verify Sheet Exists**:
   - Call `SheetsInformation.getSheetProperties()` to verify sheet exists
   - Returns null if sheet doesn't exist
   - Throws exception with message "Invalid sheet name is provided"

4. **Execute Append Operation**:
   - Call `Spreadsheet.appendCellDataToSheet()` with retry logic
   - Uses Google Sheets API `values().append()` method
   - Google automatically finds next available row

5. **API Call Details**:
   - Endpoint: `spreadsheets.values.append`
   - Range: Sheet name only (e.g., "Sheet1")
   - ValueInputOption: `USER_ENTERED` (allows formulas and auto-formatting)
   - Uses `GoogleApiExecutor` for automatic retry

6. **Return Result**:
   - Returns number of cells updated as Double
   - Example: 2 rows × 3 columns = 6.0 cells

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Append cells
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Cells Updated**: Count of cells modified
- **Errors**: Any exceptions with stack traces

## Troubleshooting

### Problem: "You have provided Invalid inputs"

**Cause**: One or more required parameters are null or empty

**Solution**:
1. Verify spreadsheet ID is not empty
2. Verify sheet name is not empty
3. Ensure cell data array is not empty

---

### Problem: "Invalid sheet name is provided"

**Cause**: Sheet name doesn't exist in the spreadsheet

**Solution**:
1. Check sheet name spelling (case-sensitive)
2. Verify sheet exists in the spreadsheet
3. Create the sheet if it doesn't exist

---

### Problem: "Spreadsheet or sheet not found"

**Cause**: Invalid spreadsheet ID

**Solution**:
1. Verify spreadsheet ID from the URL
2. Ensure spreadsheet hasn't been deleted
3. Check you have access to the spreadsheet

---

### Problem: Data appears in wrong columns

**Cause**: Column count mismatch with existing data

**Solution**:
1. Count columns in existing data
2. Ensure new rows have same number of columns
3. Add empty strings for missing columns

---

### Problem: Append operation slow

**Cause**: Large number of rows or network latency

**Solution**:
1. Batch rows into smaller groups
2. Check network connectivity
3. Monitor API quota usage

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data to specific range
- [Home](/) - Extension overview

