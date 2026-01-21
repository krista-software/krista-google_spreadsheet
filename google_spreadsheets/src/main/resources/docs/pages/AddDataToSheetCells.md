# Add Data To Sheet Cells

## Overview

The **Add Data To Sheet Cells** catalog request adds or updates data in a specific cell range within a Google Sheets spreadsheet. You specify the exact range using top-left and bottom-right cell coordinates, and the operation writes the provided data to that range.

This request is ideal for updating specific sections of a spreadsheet, filling in templates, or modifying existing data in precise locations.

## Request Details

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_21166842-ada9-4fb1-8fde-95600920d89c` |
| **Name** | Add data to sheet cells |
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet | Must not be null or empty | `Sheet1` |
| Top left cell | Text | Yes | The top-left cell of the range (e.g., A1) | Must be valid cell ID format; cannot be >= bottom right cell | `A1` |
| Bottom right cell | Text | Yes | The bottom-right cell of the range (e.g., D5) | Must be valid cell ID format; must be > top left cell | `D5` |
| Cell data | [ [ Text ] ] | Yes | Two-dimensional array of data to add | Must not be null or empty | `[["Name", "Age"], ["John", "30"]]` |

### Parameter Details

**Spreadsheet Id**:
- Found in the spreadsheet URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
- Must be a valid Google Sheets spreadsheet ID
- User must have edit access to the spreadsheet

**Sheet name**:
- Case-sensitive sheet name
- Must exist in the spreadsheet

**Top left cell** and **Bottom right cell**:
- Valid cell ID format: Letter(s) followed by number(s) (e.g., A1, B2, AA10)
- Top left cell must be before bottom right cell (alphabetically and numerically)
- Defines the rectangular range to update

**Cell data**:
- Two-dimensional array: `[[row1_col1, row1_col2], [row2_col1, row2_col2]]`
- Data dimensions should match the specified range
- If data is smaller than range, remaining cells are left empty
- If data is larger than range, excess data is ignored

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Add data to sheet cell result | Number | The total number of cells that were updated | `8.0` |

### Output Details

**Success Response**:
Returns the count of cells updated as a number.

**Example**:
- Range A1:D2 (2 rows × 4 columns) = 8 cells updated
- Output: `8.0`

## Validation Rules

The following validation rules are enforced:

1. **Spreadsheet ID Validation**:
   - Must not be null or empty
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

2. **Sheet Name Validation**:
   - Must not be null or empty
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

3. **Cell Range Validation**:
   - Top left cell and bottom right cell must not be null or empty
   - Must match pattern: `[a-zA-Z]+[0-9]+`
   - Top left cell must be less than bottom right cell
   - Violations throw: `IllegalArgumentException` with specific messages:
     - "Invalid top left cell id or bottom right cell id."
     - "Top left cell id cannot be greater than bottom right cell id."

4. **Cell Data Validation**:
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

### Example 1: Update Header Row

**Scenario**: Update the header row of a data table.

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Sales Data`
- Top left cell: `A1`
- Bottom right cell: `D1`
- Cell data: `[["Date", "Product", "Quantity", "Revenue"]]`

**Result**: `4.0`

### Example 2: Fill Data Block

**Scenario**: Add a 3x3 block of data starting at cell B2.

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Inventory`
- Top left cell: `B2`
- Bottom right cell: `D4`
- Cell data: `[["Item1", "10", "100"], ["Item2", "20", "200"], ["Item3", "30", "300"]]`

**Result**: `9.0`

### Example 3: Update Single Cell

**Scenario**: Update a single cell value.

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Dashboard`
- Top left cell: `A1`
- Bottom right cell: `A1`
- Cell data: `[["Updated Value"]]`

**Result**: `1.0`

## Business Rules

1. **Range Overwrite**:
   - All cells in the specified range are updated
   - Existing data in the range is overwritten
   - Data outside the range is not affected

2. **Data Fitting**:
   - If data is smaller than range, remaining cells are cleared
   - If data is larger than range, excess data is ignored
   - Best practice: match data dimensions to range

3. **Cell Formatting**:
   - Data is entered using USER_ENTERED mode
   - Google Sheets auto-detects data types (numbers, dates, formulas)
   - Formulas are evaluated if entered as text starting with =

## Limitations

1. **Range Specification Required**:
   - Must specify exact range
   - Cannot auto-detect data dimensions
   - Use "Append Row Data" for automatic placement

2. **Data Dimension Mismatch**:
   - Excess data is silently ignored
   - No warning if data doesn't fit range
   - Validate dimensions before calling

3. **Cell Limits**:
   - Google Sheets maximum: 10 million cells per spreadsheet
   - Maximum columns per sheet: 18,278 (column ZZZ)
   - Maximum rows per sheet: 40,000 (for performance)

## Best Practices

1. **Validate Cell Range**:
   - Ensure top left cell is before bottom right cell
   - Use valid cell ID format (e.g., A1, not 1A)
   - Calculate range dimensions before calling

2. **Match Data to Range**:
   - Count rows and columns in your data
   - Calculate appropriate cell range
   - Avoid data dimension mismatches

3. **Handle Formulas**:
   - Prefix formulas with = sign
   - Test formulas before bulk operations
   - Consider formula dependencies

4. **Batch Updates**:
   - Combine multiple cell updates when possible
   - Minimize API calls for better performance
   - Use appropriate range sizes

## Common Use Cases

1. **Template Population**:
   - Filling in spreadsheet templates
   - Updating report sections
   - Populating forms with data

2. **Data Updates**:
   - Modifying specific data ranges
   - Updating calculated fields
   - Refreshing dashboard values

3. **Bulk Edits**:
   - Updating multiple cells at once
   - Replacing data blocks
   - Correcting data ranges

4. **Report Generation**:
   - Writing report data to specific sections
   - Updating summary tables
   - Filling in analysis results

## Related Catalog Requests

- [Append Row Data At The End Of Spreadsheet](pages/AppendRowDataAtTheEndOfSpreadsheet.md) - Append without specifying range
- [Add Data Rows From CSV](pages/AddDataRowsFromCsv.md) - Import CSV data
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear cells before adding data
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve data from range

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `addDataToSheetCells(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell, List<List<String>> cellData)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check all parameters are not null/empty
   - Validate cell IDs using `ValidationHelper.validateCellIds()`
   - Throws `IllegalArgumentException` if validation fails

2. **Create ExcelSheet Object**:
   - Construct with spreadsheet ID, sheet name, and cell range

3. **Execute Update Operation**:
   - Call `Spreadsheet.addDataToSheet()` with retry logic
   - Uses Google Sheets API `values().update()` method
   - Range format: `Sheet1!A1:D5`

4. **API Call Details**:
   - Endpoint: `spreadsheets.values.update`
   - ValueInputOption: `USER_ENTERED` (enables formulas and auto-formatting)
   - Uses `GoogleApiExecutor` for automatic retry

5. **Return Result**:
   - Returns number of cells updated as Double

### Telemetry Metrics

- **Operation**: Update cells
- **Spreadsheet ID**: Logged for traceability
- **Sheet Name**: Logged for traceability
- **Range**: Top left and bottom right cells
- **Cells Updated**: Count of cells modified

## Troubleshooting

### Problem: "Invalid top left cell id or bottom right cell id"

**Cause**: Cell ID format is invalid

**Solution**:
1. Use format: Letter(s) + Number(s) (e.g., A1, B2, AA10)
2. Ensure no spaces or special characters
3. Check cell IDs are not null or empty

---

### Problem: "Top left cell id cannot be greater than bottom right cell id"

**Cause**: Cell range is reversed

**Solution**:
1. Ensure top left cell comes before bottom right cell
2. Example: A1 should be top left, D5 should be bottom right
3. Not: D5 as top left, A1 as bottom right

---

### Problem: Data doesn't appear in expected cells

**Cause**: Data dimensions don't match range

**Solution**:
1. Calculate range size: (bottom row - top row + 1) × (bottom col - top col + 1)
2. Ensure data array has matching dimensions
3. Add empty strings for missing cells

---

### Problem: Formulas not evaluating

**Cause**: Formula not prefixed with = sign

**Solution**:
1. Ensure formulas start with = (e.g., "=SUM(A1:A10)")
2. Check formula syntax is correct
3. Verify cell references are valid

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Append Row Data At The End Of Spreadsheet](pages/AppendRowDataAtTheEndOfSpreadsheet.md) - Alternative for appending
- [Home](/) - Extension overview

