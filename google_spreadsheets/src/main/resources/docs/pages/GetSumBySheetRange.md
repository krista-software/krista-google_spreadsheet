# Get Sum By Sheet Range

## Overview

The **Get Sum By Sheet Range** catalog request calculates the sum of all numeric values within a specified cell range in a Google Sheets spreadsheet. Non-numeric values are automatically ignored during the calculation.

This request is ideal for calculating totals, aggregating numeric data, or performing quick summations without formulas.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_5e6e2fdb-20a9-4638-b0ab-884dfe56476b` |
| **Name** | Get sum by sheet range |
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
- Top left cell must be before bottom right cell
- Defines the rectangular range for summation
- Examples: A1:D5, B2:F10, AA1:ZZ100

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Sum result | Number | The sum of all numeric values in the range | `1250.50` |

### Output Details

**Success Response**:
Returns the sum as a number (Double).

**Example**:
```
1250.50
```

**Empty or Non-Numeric Range**:
If the range contains no numeric values, returns: `0.0`

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
   - Violations throw: `IllegalArgumentException` with specific messages

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

### Example 1: Calculate Total Sales

**Scenario**: Sum all sales values in a column.

**Sheet Data**:
```
A1: Date       | B1: Product  | C1: Amount
A2: 2024-01-01 | Widget A     | 100.00
A3: 2024-01-02 | Widget B     | 75.50
A4: 2024-01-03 | Widget C     | 200.00
A5: 2024-01-04 | Widget D     | 125.00
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Sales`
- Top left cell: `C2`
- Bottom right cell: `C5`

**Result**: `500.50`

### Example 2: Sum Mixed Data

**Scenario**: Sum a range containing both numbers and text.

**Sheet Data**:
```
A1: Item   | B1: Value
A2: Item 1 | 50
A3: Item 2 | N/A
A4: Item 3 | 75
A5: Item 4 | 25
```

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Data`
- Top left cell: `B2`
- Bottom right cell: `B5`

**Result**: `150.0` (N/A is ignored)

### Example 3: Sum Entire Table

**Scenario**: Calculate sum of all numeric values in a table.

**Sheet Data**:
```
A1: Q1  | B1: Q2  | C1: Q3  | D1: Q4
A2: 100 | B2: 150 | C2: 200 | D2: 175
A3: 125 | B3: 175 | C3: 225 | D3: 200
```

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Quarterly Sales`
- Top left cell: `A2`
- Bottom right cell: `D3`

**Result**: `1350.0`

## Business Rules

1. **Automatic Type Filtering**:
   - Only numeric values are included in sum
   - Text, dates, and empty cells are ignored
   - No error thrown for non-numeric values

2. **Number Parsing**:
   - Values are parsed as doubles
   - Decimal numbers are supported
   - Invalid numbers are silently ignored

3. **Empty Cells**:
   - Empty cells are treated as zero (ignored)
   - Do not affect the sum
   - No error for empty ranges

4. **Read-Only Operation**:
   - Does not modify spreadsheet data
   - Safe to call repeatedly
   - No side effects

## Limitations

1. **No Formula Evaluation**:
   - Sums the displayed values, not formulas
   - If cells contain formulas, their results are summed
   - Cannot sum formula text itself

2. **String to Number Conversion**:
   - Uses simple parsing (Double.parseDouble)
   - Invalid formats are silently ignored
   - No custom number format support

3. **Large Ranges**:
   - Very large ranges may be slow
   - Consider breaking into smaller ranges
   - API quota limits apply

4. **Precision**:
   - Uses double precision floating point
   - May have rounding errors for very large numbers
   - Consider precision requirements

## Best Practices

1. **Specify Exact Range**:
   - Use precise cell coordinates
   - Avoid including header rows
   - Include only numeric data columns

2. **Handle Zero Results**:
   - Check if result is 0.0
   - Determine if range is empty or all non-numeric
   - Provide appropriate feedback

3. **Data Validation**:
   - Ensure range contains expected numeric data
   - Validate result is within expected bounds
   - Handle unexpected values

4. **Performance Optimization**:
   - Use smallest range necessary
   - Cache results if data doesn't change frequently
   - Consider API quota usage

5. **Error Handling**:
   - Handle API errors gracefully
   - Implement retry for transient failures
   - Validate inputs before calling

## Common Use Cases

1. **Financial Calculations**:
   - Calculate total revenue
   - Sum expenses
   - Aggregate financial data

2. **Data Analysis**:
   - Calculate totals for reports
   - Aggregate metrics
   - Sum survey responses

3. **Inventory Management**:
   - Calculate total stock
   - Sum quantities
   - Aggregate inventory values

4. **Reporting**:
   - Generate summary statistics
   - Calculate subtotals
   - Aggregate data for dashboards

## Related Catalog Requests

- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve all data from range
- [Find Values In Sheet](pages/FindValuesInSheet.md) - Search for specific values
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Write calculated results
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear data from range

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `getSumBySheetRange(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check all parameters are not null/empty using `validateInputParamsForRange()`
   - Validate cell IDs using `ValidationHelper.validateCellIds()`
   - Throws `IllegalArgumentException` if validation fails

2. **Create ExcelSheet Object**:
   - Construct with spreadsheet ID, sheet name, and cell range

3. **Verify Sheet Exists**:
   - Call `SheetsInformation.getSheetProperties()` to verify sheet exists
   - Returns null if sheet doesn't exist
   - Throws exception with message "Invalid sheet name is provided"

4. **Retrieve Data and Calculate Sum**:
   - Call `SheetsInformation.getSumByCellRange()` to get sum
   - Retrieves all data from range using Google Sheets API
   - Iterates through all cells

5. **Summation Algorithm**:
   - Initialize total to 0.0
   - For each cell value:
     - Try to parse as Double
     - If successful, add to total
     - If parsing fails (NumberFormatException), ignore and continue
   - Return final total

6. **API Call Details**:
   - Endpoint: `spreadsheets.values.get`
   - Uses `GoogleApiExecutor` for automatic retry
   - Returns `ValueRange` object with data

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Get sum
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Range**: Top left and bottom right cells
- **Sum Result**: Calculated sum value

## Troubleshooting

### Problem: "Invalid sheet name is provided"

**Cause**: Sheet name doesn't exist in the spreadsheet

**Solution**:
1. Check sheet name spelling (case-sensitive)
2. Verify sheet exists in the spreadsheet
3. List all sheets to confirm name

---

### Problem: Sum is 0.0 but range has values

**Cause**: All values in range are non-numeric

**Solution**:
1. Verify cells contain numeric values
2. Check for text formatting on numbers
3. Ensure numbers are not stored as text

---

### Problem: Sum is incorrect

**Cause**: Range includes non-numeric cells or wrong range

**Solution**:
1. Verify range coordinates are correct
2. Check for hidden rows/columns
3. Ensure all expected cells are included

---

### Problem: Floating point precision issues

**Cause**: Double precision limitations

**Solution**:
1. Round result to appropriate decimal places
2. Use BigDecimal for high precision requirements
3. Consider precision requirements

---

### Problem: Sum is too slow

**Cause**: Large range being processed

**Solution**:
1. Reduce range size
2. Break into smaller ranges
3. Consider caching results

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve data
- [Find Values In Sheet](pages/FindValuesInSheet.md) - Search for values
- [Home](/) - Extension overview

