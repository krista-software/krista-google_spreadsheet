# Clear Spreadsheet Cells

## Overview

The **Clear Spreadsheet Cells** catalog request clears all data from a specified cell range in a Google Sheets spreadsheet. This operation removes cell values while preserving formatting, formulas structure, and other cell properties.

This request is ideal for resetting data sections, clearing temporary data, or preparing ranges for new data without affecting the spreadsheet's structure.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_83d11bf1-1d7d-43fe-b84f-d4b00d98ddc8` |
| **Name** | Clear spreadsheet cells |
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Top left cell | Text | Yes | The top-left cell of the range to clear (e.g., A1) | Must be valid cell ID format; cannot be >= bottom right cell | `A1` |
| Bottom right cell | Text | Yes | The bottom-right cell of the range to clear (e.g., D5) | Must be valid cell ID format; must be > top left cell | `D5` |

### Parameter Details

**Spreadsheet Id**:
- Found in the spreadsheet URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
- Must be a valid Google Sheets spreadsheet ID
- User must have edit access to the spreadsheet

**Sheet name**:
- Case-sensitive sheet name
- Must exist in the spreadsheet
- If the sheet doesn't exist, returns error: "Invalid sheet name is provided"

**Top left cell** and **Bottom right cell**:
- Valid cell ID format: Letter(s) followed by number(s) (e.g., A1, B2, AA10)
- Top left cell must be before bottom right cell (alphabetically and numerically)
- Defines the rectangular range to clear
- Examples: A1:D5, B2:F10, AA1:ZZ100

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Clear cells result | Text | Success or failure message | `Cell range cleared successfully` |

### Output Details

**Success Response**:
```
Cell range cleared successfully
```

**Failure Response**:
```
Cell range not cleared
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

3. **Cell Range Validation**:
   - Top left cell and bottom right cell must not be null or empty
   - Must match pattern: `[a-zA-Z]+[0-9]+`
   - Top left cell must be less than bottom right cell
   - Violations throw: `IllegalArgumentException` with specific messages:
     - "Invalid top left cell id or bottom right cell id."
     - "Top left cell id cannot be greater than bottom right cell id."

4. **Spreadsheet Access**:
   - User must have edit permissions
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
Cell range not cleared
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

### Example 1: Clear Data Range for New Import

**Scenario**: Clear existing data before importing new monthly sales data.

**Existing Data in Sheet**:
```
A1: Date       | B1: Product  | C1: Quantity | D1: Revenue
A2: 2024-01-01 | Widget A     | 10           | 100.00
A3: 2024-01-02 | Widget B     | 5            | 75.00
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Sales`
- Top left cell: `A2`
- Bottom right cell: `D3`

**Result**: `Cell range cleared successfully`

**Resulting Sheet**:
```
A1: Date       | B1: Product  | C1: Quantity | D1: Revenue
A2:            | B2:          | C2:          | D2:
A3:            | B3:          | C3:          | D3:
```

### Example 2: Clear Temporary Calculation Area

**Scenario**: Clear a temporary calculation area after processing.

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Calculations`
- Top left cell: `F1`
- Bottom right cell: `J20`

**Result**: `Cell range cleared successfully`

### Example 3: Reset Form Input Area

**Scenario**: Clear form input cells to prepare for new entry.

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Form`
- Top left cell: `B5`
- Bottom right cell: `D10`

**Result**: `Cell range cleared successfully`

## Business Rules

1. **Data Removal Only**:
   - Only cell values are cleared
   - Cell formatting (colors, borders, fonts) is preserved
   - Data validation rules remain intact
   - Conditional formatting is not affected

2. **Range Specification**:
   - Must specify exact range to clear
   - Cannot clear entire sheet with this request
   - Use specific cell coordinates

3. **Undo Support**:
   - Cleared data can be recovered using Google Sheets undo (Ctrl+Z)
   - Consider backing up important data before clearing
   - No automatic backup is created

4. **Concurrent Operations**:
   - Safe to use with other operations
   - Clearing doesn't lock the spreadsheet
   - Multiple users can work simultaneously

## Limitations

1. **Formatting Preservation**:
   - Cannot clear formatting with this request
   - Only clears cell values
   - Use Google Sheets UI to clear formatting

2. **No Selective Clear**:
   - Clears all cells in range
   - Cannot skip specific cells
   - Cannot clear based on conditions

3. **Sheet Size Limits**:
   - Google Sheets maximum: 10 million cells per spreadsheet
   - Maximum columns per sheet: 18,278 (column ZZZ)
   - Maximum rows per sheet: 40,000 (for performance)

4. **No Undo API**:
   - Cannot programmatically undo clear operation
   - Must use Google Sheets UI for undo
   - Consider validation before clearing

## Best Practices

1. **Verify Range Before Clearing**:
   - Double-check cell coordinates
   - Ensure you're clearing the intended range
   - Test with small ranges first

2. **Preserve Headers**:
   - Start clear range below header row
   - Example: Clear A2:D100, not A1:D100
   - Keeps column headers intact

3. **Backup Important Data**:
   - Export data before clearing large ranges
   - Consider creating a backup sheet
   - Document clearing operations

4. **Use Appropriate Range**:
   - Clear only necessary cells
   - Avoid clearing entire columns/rows unnecessarily
   - Minimize API calls

5. **Combine with Other Operations**:
   - Clear before adding new data
   - Clear after data export
   - Clear temporary calculation areas

## Common Use Cases

1. **Data Refresh**:
   - Clear old data before importing new data
   - Reset daily/weekly/monthly data ranges
   - Prepare for data updates

2. **Form Reset**:
   - Clear form input fields
   - Reset user entry areas
   - Prepare for new submissions

3. **Temporary Data Cleanup**:
   - Clear calculation scratch areas
   - Remove temporary processing data
   - Clean up intermediate results

4. **Report Preparation**:
   - Clear previous report data
   - Reset report sections
   - Prepare for new report generation

## Related Catalog Requests

- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data after clearing
- [Delete Sheet Rows](pages/DeleteSheetRows.md) - Delete entire rows
- [Delete Sheet Columns](pages/DeleteSheetColumns.md) - Delete entire columns
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve data before clearing

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `clearSpreadsheetCells(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell)`
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

4. **Execute Clear Operation**:
   - Call `Spreadsheet.clearSpreadSheetByCellRange()` with retry logic
   - Uses Google Sheets API `values().batchClear()` method
   - Range format: `Sheet1!A1:D5`

5. **API Call Details**:
   - Endpoint: `spreadsheets.values.batchClear`
   - Request body: `BatchClearValuesRequest` with range list
   - Uses `GoogleApiExecutor` for automatic retry

6. **Return Result**:
   - Returns "Cell range cleared successfully" if successful
   - Returns "Cell range not cleared" if operation fails

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Clear cells
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Range**: Top left and bottom right cells
- **Cleared Ranges**: List of ranges cleared

## Troubleshooting

### Problem: "Invalid sheet name is provided"

**Cause**: Sheet name doesn't exist in the spreadsheet

**Solution**:
1. Check sheet name spelling (case-sensitive)
2. Verify sheet exists in the spreadsheet
3. List all sheets to confirm name

---

### Problem: "Cell range not cleared"

**Cause**: API call failed or returned null

**Solution**:
1. Check internet connectivity
2. Verify spreadsheet permissions
3. Retry the operation
4. Check Google Sheets API status

---

### Problem: "Invalid top left cell id or bottom right cell id"

**Cause**: Cell ID format is invalid

**Solution**:
1. Use format: Letter(s) + Number(s) (e.g., A1, B2, AA10)
2. Ensure no spaces or special characters
3. Check cell IDs are not null or empty

---

### Problem: Formatting also cleared

**Cause**: This is not the expected behavior of this request

**Solution**:
1. This request only clears values, not formatting
2. If formatting is cleared, it may be a different operation
3. Check if other operations are running

---

### Problem: Cannot clear entire sheet

**Cause**: This request requires specific range

**Solution**:
1. Calculate the full range of your sheet
2. Use appropriate top left (A1) and bottom right cell
3. Example: A1:Z1000 for a large range

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data after clearing
- [Home](/) - Extension overview

