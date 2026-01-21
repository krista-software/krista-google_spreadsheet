# Add Empty Sheet Columns

## Overview

The **Add Empty Sheet Columns** catalog request inserts one or more empty columns into a Google Sheets spreadsheet at a specified position. This operation shifts existing columns to the right to make room for the new columns.

This request is ideal for adding space for new data fields, restructuring spreadsheets, or inserting columns between existing data.

## Request Details

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_bfbc797a-6d9c-4d0e-b5a2-2a6096ca76b9` |
| **Name** | Add empty sheet columns |
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Column start index | Number | Yes | The starting column index where insertion begins (0-based) | Must not be null or negative | `3.0` |
| Column end index | Number | Yes | The ending column index (0-based, exclusive) | Must not be null or negative; must be > start index | `6.0` |

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
- **End index is exclusive**: To add 3 columns starting at D, use start=3, end=6
- **Number of columns added**: end - start
- **Insertion point**: New columns are inserted at the start index position
- Examples:
  - Add 1 column at position D: start=3, end=4
  - Add 3 columns at position C: start=2, end=5
  - Add 2 columns at position A: start=0, end=2

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Add empty columns result | Text | Success message indicating columns were added | `COLUMNS added successfully` |

### Output Details

**Success Response**:
```
COLUMNS added successfully
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

### Example 1: Add Single Column

**Scenario**: Insert a new "Email" column between "Name" and "Phone" columns.

**Before**:
```
A: ID | B: Name | C: Phone | D: Address
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Contacts`
- Column start index: `2.0` (Insert at position C)
- Column end index: `3.0` (Add 1 column)

**Result**: `COLUMNS added successfully`

**After**:
```
A: ID | B: Name | C: (empty) | D: Phone | E: Address
```

### Example 2: Add Multiple Columns

**Scenario**: Add 3 empty columns at the beginning for new data fields.

**Before**:
```
A: Name | B: Age | C: City
```

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Employees`
- Column start index: `0.0` (Insert at position A)
- Column end index: `3.0` (Add 3 columns)

**Result**: `COLUMNS added successfully`

**After**:
```
A: (empty) | B: (empty) | C: (empty) | D: Name | E: Age | F: City
```

### Example 3: Add Columns in Middle

**Scenario**: Insert 2 columns between existing data columns.

**Before**:
```
A: Q1 | B: Q2 | C: Total
```

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Sales`
- Column start index: `2.0` (Insert at position C)
- Column end index: `4.0` (Add 2 columns)

**Result**: `COLUMNS added successfully`

**After**:
```
A: Q1 | B: Q2 | C: (empty) | D: (empty) | E: Total
```

## Business Rules

1. **Column Shifting**:
   - Existing columns at and after the insertion point shift right
   - Column references in formulas update automatically
   - Absolute column references (e.g., $C$1) also update

2. **Empty Columns**:
   - New columns contain no data
   - No formatting is applied
   - Ready for data entry or formulas

3. **Zero-Based Indexing**:
   - Column A = index 0
   - Column B = index 1
   - Always use 0-based indexing

4. **Exclusive End Index**:
   - End index is not included
   - Number of columns added = end - start
   - To add N columns: end = start + N

## Limitations

1. **Maximum Columns**:
   - Google Sheets maximum: 18,278 columns per sheet (column ZZZ)
   - Cannot exceed this limit
   - Check current column count before adding

2. **No Data or Formatting**:
   - Columns are completely empty
   - No headers or default values
   - Must populate separately

3. **Formula Updates**:
   - Formulas update automatically but may need verification
   - Complex formulas may require manual adjustment
   - Test formulas after insertion

4. **Protected Ranges**:
   - Cannot insert columns in protected ranges
   - Remove protection before insertion
   - Check permissions first

## Best Practices

1. **Verify Column Indices**:
   - Double-check start and end indices
   - Remember: 0-based indexing
   - Calculate number of columns: end - start

2. **Plan Column Layout**:
   - Determine where new columns should go
   - Consider impact on existing data
   - Document column purposes

3. **Add Headers After Insertion**:
   - Populate header row for new columns
   - Use descriptive column names
   - Maintain consistent formatting

4. **Check Formula Dependencies**:
   - Review formulas that may be affected
   - Test calculations after insertion
   - Update any hardcoded column references

5. **Batch Operations**:
   - Add multiple columns in one operation
   - Minimize API calls
   - Plan insertions efficiently

## Common Use Cases

1. **Data Expansion**:
   - Add fields for new data types
   - Insert calculation columns
   - Add space for derived data

2. **Spreadsheet Restructuring**:
   - Reorganize column layout
   - Insert separator columns
   - Add grouping columns

3. **Template Preparation**:
   - Add columns for future data
   - Create space for user input
   - Prepare for data import

4. **Report Enhancement**:
   - Add columns for additional metrics
   - Insert comparison columns
   - Add space for annotations

## Related Catalog Requests

- [Delete Sheet Columns](pages/DeleteSheetColumns.md) - Remove columns
- [Add Empty Sheet Rows](pages/AddEmptySheetRows.md) - Add rows instead of columns
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Populate new columns with data
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve data after modification

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `addEmptySheetColumns(String spreadsheetId, String sheetName, Double columnStartIndex, Double columnEndIndex)`
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
   - Returns "Invalid sheet name is provided" if null

4. **Execute Insert Operation**:
   - Call `Spreadsheet.addEmptyColumnsOrRowsToSheet()` with dimension="COLUMNS"
   - Creates `InsertDimensionRequest` with sheet ID and column range
   - Uses Google Sheets API `batchUpdate()` method

5. **API Call Details**:
   - Endpoint: `spreadsheets.batchUpdate`
   - Request: `InsertDimensionRequest` with COLUMNS dimension
   - Uses `GoogleApiExecutor` for automatic retry

6. **Return Result**:
   - Returns "COLUMNS added successfully" on success

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Add empty columns
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Column Range**: Start and end indices
- **Columns Added**: Count (end - start)

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

### Problem: Columns inserted at wrong position

**Cause**: Incorrect column index (0-based indexing confusion)

**Solution**:
1. Remember: Column A = 0, B = 1, C = 2, etc.
2. Columns are inserted AT the start index
3. Existing columns shift right

---

### Problem: Wrong number of columns added

**Cause**: Incorrect calculation of end index

**Solution**:
1. Number of columns = end - start
2. To add 3 columns: end = start + 3
3. End index is exclusive

---

### Problem: Cannot add columns

**Cause**: Maximum column limit reached or protected range

**Solution**:
1. Check current column count (max 18,278)
2. Verify no protected ranges at insertion point
3. Check edit permissions

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Delete Sheet Columns](pages/DeleteSheetColumns.md) - Remove columns
- [Add Empty Sheet Rows](pages/AddEmptySheetRows.md) - Add rows
- [Home](/) - Extension overview

