# Add Empty Sheet Rows

## Overview

The **Add Empty Sheet Rows** catalog request inserts one or more empty rows into a Google Sheets spreadsheet at a specified position. This operation shifts existing rows downward to make room for the new rows.

This request is ideal for adding space for new records, restructuring spreadsheets, or inserting rows between existing data.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_be2e3758-ec2e-441a-918e-49d5c41cb54f` |
| **Name** | Add empty sheet rows |
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Row start index | Number | Yes | The starting row index where insertion begins (0-based) | Must not be null or negative | `5.0` |
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
- **End index is exclusive**: To add 5 rows starting at row 6, use start=5, end=10
- **Number of rows added**: end - start
- **Insertion point**: New rows are inserted at the start index position
- Examples:
  - Add 1 row at position 5: start=4, end=5
  - Add 5 rows at position 10: start=9, end=14
  - Add 3 rows at position 1: start=0, end=3

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Add empty rows result | Text | Success message indicating rows were added | `ROWS added successfully` |

### Output Details

**Success Response**:
```
ROWS added successfully
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

### Example 1: Add Single Row

**Scenario**: Insert a blank row between header and data for spacing.

**Before**:
```
Row 1: Name | Age | City
Row 2: Alice | 30 | NYC
Row 3: Bob | 25 | LA
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Employees`
- Row start index: `1.0` (Insert at row 2)
- Row end index: `2.0` (Add 1 row)

**Result**: `ROWS added successfully`

**After**:
```
Row 1: Name | Age | City
Row 2: (empty)
Row 3: Alice | 30 | NYC
Row 4: Bob | 25 | LA
```

### Example 2: Add Multiple Rows

**Scenario**: Add 10 empty rows for manual data entry.

**Before**:
```
Row 1: Product | Price | Stock
Row 2: Widget A | 10.00 | 100
```

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Inventory`
- Row start index: `2.0` (Insert at row 3)
- Row end index: `12.0` (Add 10 rows)

**Result**: `ROWS added successfully`

**After**:
```
Row 1: Product | Price | Stock
Row 2: Widget A | 10.00 | 100
Row 3-12: (empty rows)
```

### Example 3: Add Rows at Beginning

**Scenario**: Insert rows at the top for new headers or notes.

**Before**:
```
Row 1: Data 1 | Data 2
Row 2: Data 3 | Data 4
```

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Sheet1`
- Row start index: `0.0` (Insert at row 1)
- Row end index: `2.0` (Add 2 rows)

**Result**: `ROWS added successfully`

**After**:
```
Row 1: (empty)
Row 2: (empty)
Row 3: Data 1 | Data 2
Row 4: Data 3 | Data 4
```

## Business Rules

1. **Row Shifting**:
   - Existing rows at and after the insertion point shift down
   - Row references in formulas update automatically
   - Absolute row references (e.g., $A$1) also update

2. **Empty Rows**:
   - New rows contain no data
   - No formatting is applied
   - Ready for data entry or formulas

3. **Zero-Based Indexing**:
   - Row 1 = index 0
   - Row 2 = index 1
   - Always use 0-based indexing

4. **Exclusive End Index**:
   - End index is not included
   - Number of rows added = end - start
   - To add N rows: end = start + N

## Limitations

1. **Maximum Rows**:
   - Google Sheets maximum: 40,000 rows per sheet (for performance)
   - Cannot exceed this limit
   - Check current row count before adding

2. **No Data or Formatting**:
   - Rows are completely empty
   - No default values
   - Must populate separately

3. **Formula Updates**:
   - Formulas update automatically but may need verification
   - Complex formulas may require manual adjustment
   - Test formulas after insertion

4. **Protected Ranges**:
   - Cannot insert rows in protected ranges
   - Remove protection before insertion
   - Check permissions first

## Best Practices

1. **Verify Row Indices**:
   - Double-check start and end indices
   - Remember: 0-based indexing
   - Calculate number of rows: end - start

2. **Preserve Headers**:
   - Avoid inserting rows before header row
   - Start insertion from row 2 or later
   - Keep column headers in row 1

3. **Plan Row Layout**:
   - Determine where new rows should go
   - Consider impact on existing data
   - Document row purposes

4. **Check Formula Dependencies**:
   - Review formulas that may be affected
   - Test calculations after insertion
   - Update any hardcoded row references

5. **Batch Operations**:
   - Add multiple rows in one operation
   - Minimize API calls
   - Plan insertions efficiently

## Common Use Cases

1. **Data Entry Preparation**:
   - Add blank rows for manual input
   - Create space for new records
   - Prepare for data import

2. **Spreadsheet Formatting**:
   - Insert spacing rows between sections
   - Add rows for subtotals or summaries
   - Create visual separation

3. **Template Creation**:
   - Add rows for future data
   - Create space for user input
   - Prepare standardized layouts

4. **Report Enhancement**:
   - Insert rows for additional data
   - Add space for calculations
   - Create room for annotations

## Related Catalog Requests

- [Delete Sheet Rows](pages/DeleteSheetRows.md) - Remove rows
- [Add Empty Sheet Columns](pages/AddEmptySheetColumns.md) - Add columns instead of rows
- [Append Row Data At The End Of Spreadsheet](pages/AppendRowDataAtTheEndOfSpreadsheet.md) - Add data rows at end
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Populate new rows with data

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `addEmptySheetRows(String spreadsheetId, String sheetName, Double rowStartIndex, Double rowEndIndex)`
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
   - Returns "Invalid sheet name is provided" if null

4. **Execute Insert Operation**:
   - Call `Spreadsheet.addEmptyColumnsOrRowsToSheet()` with dimension="ROWS"
   - Creates `InsertDimensionRequest` with sheet ID and row range
   - Uses Google Sheets API `batchUpdate()` method

5. **API Call Details**:
   - Endpoint: `spreadsheets.batchUpdate`
   - Request: `InsertDimensionRequest` with ROWS dimension
   - Uses `GoogleApiExecutor` for automatic retry

6. **Return Result**:
   - Returns "ROWS added successfully" on success

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Add empty rows
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Row Range**: Start and end indices
- **Rows Added**: Count (end - start)

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

### Problem: Rows inserted at wrong position

**Cause**: Incorrect row index (0-based indexing confusion)

**Solution**:
1. Remember: Row 1 = 0, Row 2 = 1, Row 3 = 2, etc.
2. Rows are inserted AT the start index
3. Existing rows shift down

---

### Problem: Wrong number of rows added

**Cause**: Incorrect calculation of end index

**Solution**:
1. Number of rows = end - start
2. To add 5 rows: end = start + 5
3. End index is exclusive

---

### Problem: Cannot add rows

**Cause**: Maximum row limit reached or protected range

**Solution**:
1. Check current row count (max 40,000)
2. Verify no protected ranges at insertion point
3. Check edit permissions

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Delete Sheet Rows](pages/DeleteSheetRows.md) - Remove rows
- [Add Empty Sheet Columns](pages/AddEmptySheetColumns.md) - Add columns
- [Home](/) - Extension overview

