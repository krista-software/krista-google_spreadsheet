# Find Values In Sheet

## Overview

The **Find Values In Sheet** catalog request searches for a specific value within a cell range in a Google Sheets spreadsheet and returns the cell addresses where the value is found. The search is case-insensitive and returns all matching cell locations.

This request is ideal for locating specific data, finding records, or identifying cells containing particular values.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_4f4ade23-d32c-4dab-ac10-10eb2ddc3c75` |
| **Name** | Find values in sheet |
| **Area** | Basic |
| **Type** | READ_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Top left cell | Text | Yes | The top-left cell of the search range (e.g., A1) | Must be valid cell ID format; cannot be >= bottom right cell | `A1` |
| Bottom right cell | Text | Yes | The bottom-right cell of the search range (e.g., D5) | Must be valid cell ID format; must be > top left cell | `D5` |
| Search value | Text | Yes | The value to search for (case-insensitive) | Must not be null or empty | `Alice` |

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
- Defines the rectangular search range

**Search value**:
- Case-insensitive search (e.g., "alice" matches "Alice", "ALICE", "alice")
- Partial matches not supported (exact match only)
- Whitespace is significant

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Found cell addresses | [ Text ] | List of cell addresses where the value was found | `["A2", "C5", "D10"]` |

### Output Details

**Success Response**:
Returns an array of cell addresses in A1 notation where the search value was found.

**Example**:
```json
["A2", "B5", "C3"]
```

**No Matches**:
If the value is not found, returns an empty list: `[]`

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

4. **Search Value Validation**:
   - Must not be null or empty
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

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

### Example 1: Find Employee Name

**Scenario**: Find all cells containing "Alice" in employee data.

**Sheet Data**:
```
A1: Name    | B1: Manager | C1: Department
A2: Alice   | B2: Bob     | C2: Engineering
A3: Bob     | B3: Alice   | C3: Sales
A4: Charlie | B4: Alice   | C4: Marketing
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Employees`
- Top left cell: `A1`
- Bottom right cell: `C4`
- Search value: `Alice`

**Result**:
```json
["A2", "B3", "B4"]
```

### Example 2: Find Product Code

**Scenario**: Locate a specific product code in inventory.

**Sheet Data**:
```
A1: Product Code | B1: Name      | C1: Stock
A2: PROD-001     | B2: Widget A  | C2: 100
A3: PROD-002     | B3: Widget B  | C3: 50
A4: PROD-001     | B4: Widget A  | C4: 25
```

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Inventory`
- Top left cell: `A1`
- Bottom right cell: `C4`
- Search value: `PROD-001`

**Result**:
```json
["A2", "A4"]
```

### Example 3: Value Not Found

**Scenario**: Search for a value that doesn't exist.

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Data`
- Top left cell: `A1`
- Bottom right cell: `D10`
- Search value: `NonExistent`

**Result**:
```json
[]
```

## Business Rules

1. **Case-Insensitive Search**:
   - Search is not case-sensitive
   - "alice", "Alice", "ALICE" all match
   - Useful for finding data regardless of capitalization

2. **Exact Match Only**:
   - Only exact matches are returned
   - Partial matches are not supported
   - "Ali" will not match "Alice"

3. **All Occurrences**:
   - Returns all cells containing the value
   - Multiple matches in same row/column are all returned
   - No limit on number of matches

4. **Read-Only Operation**:
   - Does not modify spreadsheet data
   - Safe to call repeatedly
   - No side effects

## Limitations

1. **No Partial Matching**:
   - Cannot search for substrings
   - Cannot use wildcards or regex
   - Must match entire cell value

2. **String Comparison Only**:
   - All values compared as strings
   - Numbers are compared as text
   - "10" and "10.0" are different

3. **Large Ranges**:
   - Searching very large ranges may be slow
   - Consider narrowing search range
   - API quota limits apply

4. **Formula Results**:
   - Searches formula results, not formulas
   - Cannot search for formula text
   - Calculated values may change

## Best Practices

1. **Narrow Search Range**:
   - Use smallest range necessary
   - Improves performance
   - Reduces API quota usage

2. **Handle Empty Results**:
   - Check if result array is empty
   - Provide appropriate feedback
   - Don't assume value exists

3. **Case Handling**:
   - Remember search is case-insensitive
   - Normalize search values if needed
   - Document case behavior

4. **Performance Optimization**:
   - Cache results if searching repeatedly
   - Batch multiple searches if possible
   - Consider alternative approaches for frequent searches

5. **Error Handling**:
   - Handle API errors gracefully
   - Implement retry for transient failures
   - Validate inputs before calling

## Common Use Cases

1. **Data Lookup**:
   - Find records by ID or name
   - Locate specific values
   - Verify data existence

2. **Data Validation**:
   - Check for duplicate values
   - Verify data integrity
   - Find inconsistencies

3. **Record Location**:
   - Find row containing specific value
   - Locate cells for updates
   - Identify data positions

4. **Reporting**:
   - Count occurrences of values
   - Generate value location reports
   - Audit data distribution

## Related Catalog Requests

- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve all data from range
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Update found cells
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear found cells
- [Get Sum By Sheet Range](pages/GetSumBySheetRange.md) - Calculate sum of range

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `findValuesInSheet(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell, String searchValue)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check all parameters are not null/empty
   - Validate cell IDs using `ValidationHelper.validateCellIds()`
   - Validate search value is not null/empty
   - Throws `IllegalArgumentException` if validation fails

2. **Create ExcelSheet Object**:
   - Construct with spreadsheet ID, sheet name, and cell range

3. **Verify Sheet Exists**:
   - Call `SheetsInformation.getSheetProperties()` to verify sheet exists
   - Returns null if sheet doesn't exist
   - Throws exception with message "Invalid sheet name is provided"

4. **Execute Search**:
   - Call `Spreadsheet.findValuesWithCellId()` to search
   - Retrieves all data from range using Google Sheets API
   - Performs case-insensitive comparison

5. **Search Algorithm**:
   - Convert search value to lowercase
   - Iterate through all cells in range
   - Compare each cell value (lowercase) with search value
   - Collect matching cell addresses

6. **Return Result**:
   - Returns list of cell addresses in A1 notation
   - Empty list if no matches found

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Find values
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Range**: Top left and bottom right cells
- **Search Value**: Logged for traceability
- **Matches Found**: Count of matching cells

## Troubleshooting

### Problem: "Invalid sheet name is provided"

**Cause**: Sheet name doesn't exist in the spreadsheet

**Solution**:
1. Check sheet name spelling (case-sensitive)
2. Verify sheet exists in the spreadsheet
3. List all sheets to confirm name

---

### Problem: No results found but value exists

**Cause**: Exact match required, or whitespace differences

**Solution**:
1. Ensure search value exactly matches cell value
2. Check for leading/trailing whitespace
3. Verify case doesn't matter but spelling does

---

### Problem: Too many results returned

**Cause**: Search value is common or range too large

**Solution**:
1. Narrow the search range
2. Use more specific search value
3. Filter results programmatically

---

### Problem: Search is slow

**Cause**: Large search range

**Solution**:
1. Reduce search range size
2. Search specific columns only
3. Consider caching results

---

### Problem: Numbers not matching

**Cause**: String comparison of numbers

**Solution**:
1. Ensure number format matches exactly
2. "10" and "10.0" are different
3. Format numbers consistently

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve data
- [Home](/) - Extension overview

