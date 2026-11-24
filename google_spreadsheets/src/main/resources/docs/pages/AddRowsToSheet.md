# Add Rows to Sheet

## Overview

The **Add Rows to Sheet** catalog request adds rows of data to the end of existing data in a Google Sheets spreadsheet. This operation automatically finds the next available row after the last row containing data and appends the new rows there.

This request accepts a dynamic list of row objects, making it suitable for scenarios where row data comes from composite or structured sources such as forms, tables, or mapped objects. Each row object's values are extracted and written as columns in the spreadsheet.

## Request Details

- **MCP Tool**: ✅ Yes (exposed to the Krista AI agent)

| Property | Value |
|----------|-------|
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet where data will be appended | Must not be null or empty; must exist in spreadsheet | `Sheet1` |
| Rows | List | Yes | A list of row objects to append. Each row can be a List, a Map (composite/key-value), or a simple value. | Must not be null | See examples below |

### Parameter Details

**Spreadsheet Id**:
- Found in the spreadsheet URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
- Must be a valid Google Sheets spreadsheet ID
- User must have edit access to the spreadsheet

**Sheet name**:
- Case-sensitive sheet name
- Must exist in the spreadsheet
- If the sheet doesn't exist, returns error: "Invalid sheet name is provided"

**Rows**:
- Accepts a list of row objects with flexible types:
  - **Map/Composite objects**: Each row is a key-value map. The values are extracted and written as columns. This is the typical format when using the Krista UI table input.
  - **List objects**: Each row is a list of cell values written directly as columns.
  - **Simple values**: Each item is written as a single-cell row.
- Null entries in the list are skipped
- Null values within a row are written as empty cells

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Append rows result | Number | The number of rows that were appended | `2.0` |

### Output Details

**Success Response**:
Returns the count of rows appended as a number.

**Example**:
- Appending 2 rows (regardless of column count) = 2 rows appended
- Output: `2.0`

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

3. **Spreadsheet Access**:
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
| 404 | The requested spreadsheet or sheet could not be found. Please verify the spreadsheet ID is correct and the sheet name exists. |
| 401 | Authentication has expired and requires renewal. Please click 'Authorize' to reconnect your Google account. |
| 403 | Access denied due to insufficient permissions. Please ensure you have edit permissions for this spreadsheet. |
| 429 | Request limit exceeded due to too many operations. Please wait 1-2 minutes before trying again. |
| 500/502/503/504 | Google Sheets service is temporarily unavailable. Please wait a few minutes and try again. |

### Retry Behavior

This request includes automatic retry for transient failures:

- **Maximum Attempts**: 3
- **Retriable Errors**: Connection timeouts, HTTP 429, 500, 502, 503, 504
- **Non-Retriable Errors**: HTTP 400, 401, 403, 404
- **Backoff Strategy**: Exponential with jitter (1s, 2s, 4s)

## Usage Examples

### Example 1: Append Rows from a Table Input (Map/Composite)

**Scenario**: Add employee records from a Krista UI table.

**Input Table**:

| Name    | Department | Salary |
|---------|-----------|--------|
| Alice   | Engineering | 95000 |
| Bob     | Marketing   | 78000 |

Each row is received as a Map: `{Name=Alice, Department=Engineering, Salary=95000}`

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Employees`
- Rows: (2 composite rows from the table)

**Result**:
```
2.0
```

### Example 2: Append Rows as Lists

**Scenario**: Append sales data where each row is a list of values.

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Sales`
- Rows: `[["2024-01-03", "Widget A", "8", "80.00"], ["2024-01-03", "Widget C", "12", "120.00"]]`

**Result**:
```
2.0
```

### Example 3: Append Simple Values

**Scenario**: Append individual status entries as single-cell rows.

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Status Log`
- Rows: `["Completed", "In Progress", "Pending"]`

**Result**:
```
3.0
```

## Differences from "Append Row Data at the End of Spreadsheet"

| Feature | Append Row Data (existing) | Add Rows to Sheet (this request) |
|---------|---------------------------|-------------------------------------|
| Input type | `[ [ Text ] ]` (nested text arrays) | `List` (flexible: Maps, Lists, or simple values) |
| Composite support | No | Yes (Map/key-value row objects) |
| Output value | Number of **cells** updated | Number of **rows** appended |
| UI table input | Not supported | Supported (rows as composite objects) |

## Business Rules

1. **Automatic Row Detection**:
   - Extension automatically finds the next available row
   - No need to specify row numbers
   - Appends immediately after the last row with data

2. **Flexible Row Conversion**:
   - Map/Composite rows: values are extracted in order as columns
   - List rows: elements are written directly as columns
   - Simple values: written as single-cell rows
   - Null rows are skipped

3. **Data Preservation**:
   - Existing data is never overwritten
   - New rows are always added at the end

## Limitations

1. **No Row Specification**: Always appends to the end. Use "Add Data To Sheet Cells" for specific row placement.
2. **Map Key Order**: When using Map/Composite rows, column order depends on the Map's iteration order.
3. **Sheet Size Limits**: Google Sheets maximum of 10 million cells per spreadsheet.

## Related Catalog Requests

- [Append Row Data At The End Of Spreadsheet](pages/AppendRowDataAtTheEndOfSpreadsheet.md) - Append with `[ [ Text ] ]` input
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data to specific cell range
- [Add Data Rows From CSV](pages/AddDataRowsFromCsv.md) - Import CSV data
- [Get Sheet Rows By Range](pages/GetSheetRowsByRange.md) - Retrieve existing data
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear data before appending

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `appendRowsOfSheet(String spreadsheetId, String sheetName, List<Object> rows)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**: Check spreadsheet ID and sheet name are not null/empty.
2. **Verify Sheet Exists**: Call `SheetsInformation.getSheetProperties()` to verify sheet exists.
3. **Convert Row Objects**: `convertObjectRows()` converts `List<Object>` to `List<List<String>>`:
   - `List` items: each element converted to String
   - `Map` items: values extracted and converted to String
   - Other items: `toString()` as single-cell row
   - Null items: skipped
4. **Execute Append Operation**: Call `Spreadsheet.appendCellDataToSheet()` with converted data.
5. **Return Row Count**: Returns the number of rows appended (not cell count).

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Home](/) - Extension overview
