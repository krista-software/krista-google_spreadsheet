# Get Sheet Rows By Range

## Overview

The **Get Sheet Rows By Range** catalog request retrieves data from a specified cell range in a Google Sheets spreadsheet. This operation returns the data as a two-dimensional array (list of lists) representing rows and columns.

This request is ideal for reading data from spreadsheets, extracting specific sections, or retrieving data for processing or analysis.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_fb1ef461-6e67-48b2-910b-359759f2640f` |
| **Name** | Get sheet rows by range |
| **Area** | Basic |
| **Type** | READ_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Top left cell | Text | Yes | The top-left cell of the range (e.g., A1) | Must be valid cell ID format; cannot be >= bottom right cell | `A1` |
| Bottom right cell | Text | Yes | The bottom-right cell of the range (e.g., D5) | Must be valid cell ID format; must be > top left cell | `D5` |

### Parameter Details

**Spreadsheet Id**:
- Found in the spreadsheet URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
- Must be a valid Google Sheets spreadsheet ID
- User must have read access to the spreadsheet

**Sheet name**:
- Case-sensitive sheet name
- Must exist in the spreadsheet
- If the sheet doesn't exist, returns error: "Invalid sheet name is provided"

**Top left cell** and **Bottom right cell**:
- Valid cell ID format: Letter(s) followed by number(s) (e.g., A1, B2, AA10)
- Top left cell must be before bottom right cell (alphabetically and numerically)
- Defines the rectangular range to retrieve
- Examples: A1:D5, B2:F10, AA1:ZZ100

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Sheet rows | [ [ Text ] ] | Two-dimensional array of cell values | `[["Name", "Age"], ["Alice", "30"], ["Bob", "25"]]` |

### Output Details

**Success Response**:
Returns a two-dimensional array where:
- Outer array represents rows
- Inner arrays represent columns within each row
- Empty cells are represented as empty strings

**Example**:
```json
[
  ["Name", "Age", "City"],
  ["Alice", "30", "NYC"],
  ["Bob", "25", "LA"]
]
```

**Empty Range**:
If the range contains no data, returns an empty list: `[]`

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

3. **Cell Range Validation**:
   - Top left cell and bottom right cell must not be null or empty
   - Must match pattern: `[a-zA-Z]+[0-9]+`
   - Top left cell must be less than bottom right cell
   - Violations throw: `IllegalArgumentException` with specific messages:
     - "Invalid top left cell id or bottom right cell id."
     - "Top left cell id cannot be greater than bottom right cell id."

4. **Spreadsheet Access**:
   - User must have read permissions
   - If access denied, returns appropriate error message

## Error Handling

### Error Types

**INPUT_ERROR** - Invalid input parameters:
```
You have provided Invalid inputs.
Invalid top left cell id or bottom right cell id.
Top left cell id cannot be greater than bottom right cell id.
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
| 403 | Access denied. You don't have permission to view this spreadsheet. |
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

### Example 1: Retrieve Data Table

**Scenario**: Get all employee data from a specific range.

**Sheet Data**:
```
A1: Name    | B1: Age | C1: Department
A2: Alice   | B2: 30  | C2: Engineering
A3: Bob     | B3: 25  | C3: Sales
A4: Charlie | B4: 35  | C4: Marketing
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Employees`
- Top left cell: `A1`
- Bottom right cell: `C4`

**Result**:
```json
[
  ["Name", "Age", "Department"],
  ["Alice", "30", "Engineering"],
  ["Bob", "25", "Sales"],
  ["Charlie", "35", "Marketing"]
]
```

### Example 2: Retrieve Partial Data

**Scenario**: Get only names and ages (columns A and B).

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Employees`
- Top left cell: `A2`
- Bottom right cell: `B4`

**Result**:
```json
[
  ["Alice", "30"],
  ["Bob", "25"],
  ["Charlie", "35"]
]
```

### Example 3: Retrieve Single Cell

**Scenario**: Get a single cell value.

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Dashboard`
- Top left cell: `A1`
- Bottom right cell: `A1`

**Result**:
```json
[
  ["Total Sales"]
]
```

## Business Rules

1. **Data Representation**:
   - Each row is represented as an array
   - Empty cells are included as empty strings
   - Data types are returned as strings

2. **Range Boundaries**:
   - Only cells within the specified range are returned
   - If range extends beyond data, empty cells are included
   - Sparse data is preserved with empty strings

3. **Read-Only Operation**:
   - Does not modify spreadsheet data
   - Safe to call repeatedly
   - No side effects

4. **Data Formatting**:
   - Values are returned as displayed in the sheet
   - Formulas are evaluated and results returned
   - Formatting (colors, fonts) is not included

## Limitations

1. **Large Ranges**:
   - Very large ranges may be slow to retrieve
   - Consider pagination for large datasets
   - Google Sheets API has quota limits

2. **Data Types**:
   - All values returned as strings
   - Type conversion must be done by caller
   - Dates, numbers, booleans are string representations

3. **Formula Results**:
   - Only formula results are returned, not formulas themselves
   - To get formulas, use different API endpoint
   - Calculated values may change between calls

4. **Empty Rows**:
   - Empty rows within range are included
   - Trailing empty rows may be omitted by Google Sheets API
   - Check array length to determine actual data

## Best Practices

1. **Specify Exact Range**:
   - Use precise cell coordinates
   - Avoid retrieving more data than needed
   - Consider data size and API quotas

2. **Handle Empty Data**:
   - Check if result is empty before processing
   - Handle empty cells (empty strings) appropriately
   - Validate data structure before use

3. **Type Conversion**:
   - Convert string values to appropriate types
   - Handle conversion errors gracefully
   - Validate data formats

4. **Error Handling**:
   - Check for null or empty results
   - Handle API errors appropriately
   - Implement retry logic for transient failures

5. **Performance Optimization**:
   - Retrieve only necessary data
   - Cache results if data doesn't change frequently
   - Use appropriate range sizes

## Common Use Cases

1. **Data Export**:
   - Export spreadsheet data to other systems
   - Generate reports from spreadsheet data
   - Backup spreadsheet data

2. **Data Analysis**:
   - Retrieve data for processing
   - Extract specific data ranges
   - Read data for calculations

3. **Data Validation**:
   - Verify spreadsheet contents
   - Check data integrity
   - Validate data formats

4. **Integration**:
   - Read data for workflow automation
   - Sync data with other systems
   - Extract data for APIs

## Related Catalog Requests

- [Find Values In Sheet](pages/FindValuesInSheet.md) - Search for specific values
- [Get Sum By Sheet Range](pages/GetSumBySheetRange.md) - Calculate sum of range
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Write data to range
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear data from range

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `getSheetRowsByRange(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check all parameters are not null/empty using `validateInputParamsForRange()`
   - Validate cell IDs using `ValidationHelper.validateCellIds()`
   - Throws `IllegalArgumentException` if validation fails

2. **Create ExcelSheet Object**:
   - Construct with spreadsheet ID, sheet name, and cell range

3. **Retrieve Data**:
   - Call `SheetsInformation.getAvailableRowsByRange()` to get data
   - Uses Google Sheets API `values().get()` method
   - Range format: `Sheet1!A1:D5`

4. **Verify Sheet Exists**:
   - Call `SheetsInformation.getSheetProperties()` to verify sheet exists
   - Returns null if sheet doesn't exist
   - Throws exception with message "Invalid sheet name is provided"

5. **Process Result**:
   - Convert `List<List<Object>>` to `List<List<String>>`
   - Handle empty results
   - Return data as two-dimensional array

6. **API Call Details**:
   - Endpoint: `spreadsheets.values.get`
   - Uses `GoogleApiExecutor` for automatic retry
   - Returns `ValueRange` object with data

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Get values
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Range**: Top left and bottom right cells
- **Rows Retrieved**: Count of rows returned

## Troubleshooting

### Problem: "Invalid sheet name is provided"

**Cause**: Sheet name doesn't exist in the spreadsheet

**Solution**:
1. Check sheet name spelling (case-sensitive)
2. Verify sheet exists in the spreadsheet
3. List all sheets to confirm name

---

### Problem: "Invalid top left cell id or bottom right cell id"

**Cause**: Cell ID format is invalid

**Solution**:
1. Use format: Letter(s) + Number(s) (e.g., A1, B2, AA10)
2. Ensure no spaces or special characters
3. Check cell IDs are not null or empty

---

### Problem: Empty result returned

**Cause**: Range contains no data or extends beyond data

**Solution**:
1. Verify range coordinates are correct
2. Check if sheet actually contains data
3. Ensure range doesn't start beyond data

---

### Problem: Missing columns in result

**Cause**: Rows have different lengths (sparse data)

**Solution**:
1. Google Sheets API omits trailing empty cells
2. Check each row's length individually
3. Pad rows with empty strings if needed

---

### Problem: Formula values not as expected

**Cause**: Formulas are evaluated at retrieval time

**Solution**:
1. Formulas return current calculated values
2. Values may change if referenced data changes
3. Use formula-specific API to get formula text

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Find Values In Sheet](pages/FindValuesInSheet.md) - Search for values
- [Get Sum By Sheet Range](pages/GetSumBySheetRange.md) - Calculate sum
- [Home](/) - Extension overview

