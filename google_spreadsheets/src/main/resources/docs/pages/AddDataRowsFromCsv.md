# Add Data Rows From CSV

## Overview

The **Add Data Rows From CSV** catalog request imports data from a CSV (Comma-Separated Values) file into a Google Sheets spreadsheet. This operation automatically determines the appropriate cell range based on the CSV file dimensions and adds all rows starting from cell A1.

This request is ideal for bulk data imports, migrating data from external systems, or populating spreadsheets with structured data from CSV exports.

## Request Details

| Property | Value |
|----------|-------|
| **Catalog Request ID** | `localDomainRequest_9d7d96d2-0781-460b-8428-c4681404f754` |
| **Name** | Add data rows From Csv |
| **Area** | Basic |
| **Type** | CHANGE_SYSTEM |
| **Retry Support** | Yes (automatic retry for transient failures) |

## Input Parameters

| Parameter Name | Type | Required | Description | Validation Rules | Example |
|---------------|------|----------|-------------|------------------|---------|
| Spreadsheet Id | Text | Yes | The unique identifier of the Google Sheets spreadsheet | Must not be null or empty | `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms` |
| Sheet name | Text | Yes | The name of the sheet within the spreadsheet where data will be added | Must not be null or empty | `Sheet1` |
| CSV file | File | Yes | The CSV file containing data to import | Must be a valid CSV file with data | `data.csv` |

### Parameter Details

**Spreadsheet Id**:
- Found in the spreadsheet URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
- Must be a valid Google Sheets spreadsheet ID
- User must have edit access to the spreadsheet

**Sheet name**:
- Case-sensitive sheet name
- Must exist in the spreadsheet
- If the sheet doesn't exist, the operation will fail

**CSV file**:
- Standard CSV format (comma-separated values)
- Can contain any number of rows and columns
- Empty CSV files will be rejected
- File is automatically parsed to determine dimensions

## Output Parameters

| Parameter Name | Type | Description | Example |
|---------------|------|-------------|---------|
| Add CSV data result | Text | Success message indicating the number of cells updated | `Data is added to sheet successfully and 12.0 number of cells updated` |

### Output Details

**Success Response**:
```
Data is added to sheet successfully and {number} number of cells updated
```

Where `{number}` is the total count of cells that were updated (rows × columns).

**Example**:
- CSV with 3 rows and 4 columns = 12 cells updated
- Output: `Data is added to sheet successfully and 12.0 number of cells updated`

## Validation Rules

The following validation rules are enforced:

1. **Spreadsheet ID Validation**:
   - Must not be null
   - Must not be empty or blank
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

2. **Sheet Name Validation**:
   - Must not be null
   - Must not be empty or blank
   - Violation throws: `IllegalArgumentException` with message "You have provided Invalid inputs."

3. **CSV File Validation**:
   - Must not be null
   - Must be a valid file
   - Must contain data (non-empty)
   - Empty CSV returns: "Provided CSV file does not contain the data"

4. **Sheet Existence**:
   - Sheet must exist in the spreadsheet
   - If sheet doesn't exist, returns user-friendly error message

5. **Spreadsheet Access**:
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
Provided CSV file does not contain the data
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

Example retry log:
```
WARN: Google API request failed (attempt 1/3) - Connection timeout. Retrying in 1000ms - Context: Add CSV data to spreadsheet ABC123, sheet: Sheet1
```

## Usage Examples

### Example 1: Import Sales Data

**Scenario**: Import monthly sales data from a CSV export into a Google Sheet.

**CSV File Content** (`sales_data.csv`):
```csv
Date,Product,Quantity,Revenue
2024-01-01,Widget A,10,100.00
2024-01-02,Widget B,5,75.00
2024-01-03,Widget A,8,80.00
```

**Request Parameters**:
- Spreadsheet Id: `1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`
- Sheet name: `Sales`
- CSV file: `sales_data.csv`

**Result**:
```
Data is added to sheet successfully and 16.0 number of cells updated
```

**Explanation**: 4 rows × 4 columns = 16 cells updated. Data is placed starting at cell A1.

### Example 2: Import Employee List

**Scenario**: Import employee directory from HR system export.

**CSV File Content** (`employees.csv`):
```csv
Employee ID,Name,Department,Email
E001,John Doe,Engineering,john@example.com
E002,Jane Smith,Marketing,jane@example.com
E003,Bob Johnson,Sales,bob@example.com
```

**Request Parameters**:
- Spreadsheet Id: `1AbCdEfGhIjKlMnOpQrStUvWxYz123456789`
- Sheet name: `Employees`
- CSV file: `employees.csv`

**Result**:
```
Data is added to sheet successfully and 16.0 number of cells updated
```

### Example 3: Import Inventory Data

**Scenario**: Update inventory spreadsheet with latest stock levels.

**CSV File Content** (`inventory.csv`):
```csv
SKU,Product Name,Quantity,Location
SKU001,Laptop,25,Warehouse A
SKU002,Mouse,150,Warehouse B
SKU003,Keyboard,75,Warehouse A
SKU004,Monitor,40,Warehouse C
```

**Request Parameters**:
- Spreadsheet Id: `1XyZ987654321AbCdEfGhIjKlMnOpQrStUv`
- Sheet name: `Inventory`
- CSV file: `inventory.csv`

**Result**:
```
Data is added to sheet successfully and 20.0 number of cells updated
```

## Business Rules

1. **Data Placement**:
   - Data always starts at cell A1
   - Existing data in the target range will be overwritten
   - Data outside the CSV range is not affected

2. **CSV Parsing**:
   - Standard CSV format (comma-separated)
   - First row is treated as data (not automatically as headers)
   - Empty cells in CSV are preserved as empty cells in the sheet

3. **Range Calculation**:
   - Range is automatically calculated based on CSV dimensions
   - If CSV has 5 rows and 3 columns, range is A1:C5
   - No manual range specification required

4. **Data Types**:
   - All CSV data is imported as text
   - Google Sheets may auto-detect and convert data types (numbers, dates)
   - Formulas in CSV are imported as text, not evaluated

## Limitations

1. **File Size**:
   - Large CSV files may take longer to process
   - Consider splitting very large files (>10,000 rows)

2. **Cell Limits**:
   - Google Sheets has a maximum of 10 million cells per spreadsheet
   - Ensure CSV import doesn't exceed this limit

3. **Starting Position**:
   - Data always starts at A1
   - Cannot specify custom starting cell
   - Use "Add Data To Sheet Cells" for custom range placement

4. **Existing Data**:
   - Overwrites existing data in the target range
   - No merge or append option
   - Backup important data before importing

5. **CSV Format**:
   - Only standard CSV format supported
   - Tab-separated or other delimiters not supported
   - Use standard CSV export from source systems

## Best Practices

1. **Validate CSV Before Import**:
   - Open CSV in a text editor to verify format
   - Ensure no extra commas or malformed rows
   - Check for consistent column count across rows

2. **Backup Existing Data**:
   - Create a copy of the spreadsheet before importing
   - Or use a separate sheet for imports
   - Prevents accidental data loss

3. **Use Descriptive Sheet Names**:
   - Name sheets clearly (e.g., "Sales_2024", "Inventory_Current")
   - Makes it easier to identify import targets
   - Reduces errors from importing to wrong sheet

4. **Handle Large Files**:
   - For files >5,000 rows, consider splitting into chunks
   - Monitor import time and adjust as needed
   - Use batch processing for very large datasets

5. **Error Handling**:
   - Implement retry logic in your workflow
   - Log import results for auditing
   - Validate data after import

## Common Use Cases

1. **Data Migration**:
   - Migrating data from legacy systems to Google Sheets
   - One-time bulk imports during system transitions

2. **Regular Data Updates**:
   - Daily/weekly imports from external systems
   - Automated reporting workflows
   - Scheduled data refreshes

3. **Bulk Data Entry**:
   - Importing survey results
   - Loading test data
   - Populating templates with data

4. **System Integration**:
   - Importing data from CRM systems
   - Loading data from ERP exports
   - Integrating with third-party tools

## Related Catalog Requests

- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Add data to specific cell range
- [Append Row Data At The End Of Spreadsheet](pages/AppendRowDataAtTheEndOfSpreadsheet.md) - Append data to existing rows
- [Clear Spreadsheet Cells](pages/ClearSpreadsheetCells.md) - Clear data before importing
- [Create Spreadsheet](pages/CreateSpreadsheet.md) - Create new spreadsheet for imports

## Technical Implementation

### Implementation Class

**Service Class**: `GoogleSheet.java`
- Method: `addDataRowsFromCsv(String spreadsheetId, String sheetName, File cSVFile)`
- Location: `app.krista.extensions.essentials.spreadsheets.google_sheets.service.GoogleSheet`

### Processing Flow

1. **Validate Input Parameters**:
   - Check spreadsheet ID and sheet name are not null/empty
   - Throws `IllegalArgumentException` if validation fails

2. **Convert File**:
   - Convert Krista File object to Java File object
   - Uses `KristaMediaClient.toJavaFile()`

3. **Parse CSV**:
   - Read CSV file using Apache Commons CSV library
   - Parse all rows into `List<List<String>>` structure
   - Uses `CSVFileHelper.getRowsFromCSVFile()`

4. **Check for Empty Data**:
   - If CSV contains no rows, return error message
   - Message: "Provided CSV file does not contain the data"

5. **Calculate Range**:
   - Determine row count and column count from parsed data
   - Calculate diagonal cells (top-left and bottom-right)
   - Uses `SheetsHelper.getDiagonalCellIds()`

6. **Create ExcelSheet Object**:
   - Construct ExcelSheet with spreadsheet ID, sheet name, and range
   - Range format: `Sheet1!A1:D5`

7. **Execute API Call**:
   - Call `Spreadsheet.addDataToSheet()` with retry logic
   - Uses `GoogleApiExecutor` for automatic retry
   - Returns number of cells updated

8. **Return Result**:
   - Success: "Data is added to sheet successfully and {count} number of cells updated"
   - Failure: "Failed to add Data"

### Telemetry Metrics

The following metrics are logged:

- **Operation**: Add CSV data
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
3. Ensure CSV file is selected

---

### Problem: "Provided CSV file does not contain the data"

**Cause**: CSV file is empty or contains no rows

**Solution**:
1. Open CSV file and verify it contains data
2. Check CSV file is not corrupted
3. Ensure CSV has at least one row of data

---

### Problem: "Spreadsheet or sheet not found"

**Cause**: Invalid spreadsheet ID or sheet name doesn't exist

**Solution**:
1. Verify spreadsheet ID from the URL
2. Check sheet name matches exactly (case-sensitive)
3. Ensure sheet exists in the spreadsheet

---

### Problem: "Access denied"

**Cause**: User doesn't have edit permissions

**Solution**:
1. Request edit access to the spreadsheet
2. Verify you're authenticated with the correct Google account
3. Check spreadsheet sharing settings

---

### Problem: Import takes too long

**Cause**: Large CSV file or slow network connection

**Solution**:
1. Split large CSV files into smaller chunks
2. Check network connectivity
3. Consider importing during off-peak hours

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication
- [Add Data To Sheet Cells](pages/AddDataToSheetCells.md) - Alternative for custom ranges
- [Home](/) - Extension overview

