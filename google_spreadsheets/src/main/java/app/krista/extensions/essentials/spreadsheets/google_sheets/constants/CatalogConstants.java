/*
 * Google Spreadsheet Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package app.krista.extensions.essentials.spreadsheets.google_sheets.constants;

public final class CatalogConstants {

    // Result key constants
    public static final String ADD_CSV_DATA_RESULT = "Add CSV data result";
    public static final String CLEAR_CELLS_RESULT = "Clear spreadsheet cells result";
    public static final String DELETE_COLUMNS_RESULT = "Delete sheet columns result";
    public static final String DELETE_ROWS_RESULT = "Delete sheet rows result";
    public static final String ADD_EMPTY_COLUMNS_RESULT = "Add empty sheet columns result";
    public static final String ADD_EMPTY_ROWS_RESULT = "Add empty sheet rows result";
    public static final String APPEND_CELLS_RESULT = "Append cells result";
    public static final String ADD_DATA_TO_CELLS_RESULT = "Add data to sheet cell result";
    public static final String DRIVE_FILE_RESULT = "Drive file";
    public static final String FIND_VALUES_RESULT = "Find values in sheet result";
    public static final String SHEET_ROWS_RESULT = "Sheet rows by range result";
    public static final String SUM_BY_RANGE_RESULT = "Get sum by range result";

    // Error message constants - User-friendly format: Operation + Problem + Resolution
    public static final String FAILED_CSV_DATA = "CSV data import failed due to file format issues or permission restrictions. Please ensure your CSV file is properly formatted and you have edit permissions for the spreadsheet.";
    public static final String FAILED_CLEAR_CELLS = "Cell clearing operation failed due to insufficient permissions or invalid cell range. Please verify you have edit access and the cell range is valid (e.g., A1:C10).";
    public static final String FAILED_DELETE_COLUMNS = "Column deletion failed due to permission restrictions or invalid column specification. Please ensure you have edit permissions and specify valid column references.";
    public static final String FAILED_DELETE_ROWS = "Row deletion failed due to permission restrictions or invalid row specification. Please ensure you have edit permissions and specify valid row numbers.";
    public static final String FAILED_ADD_EMPTY_COLUMNS = "Column addition failed due to permission restrictions or sheet limitations. Please verify you have edit permissions and the sheet hasn't reached the maximum column limit.";
    public static final String FAILED_ADD_EMPTY_ROWS = "Row addition failed due to permission restrictions or sheet limitations. Please verify you have edit permissions and the sheet hasn't reached the maximum row limit.";
    public static final String FAILED_APPEND_CELLS = "Data append operation failed due to permission issues or invalid data format. Please ensure you have edit permissions and the data is in the correct format for the target cells.";
    public static final String FAILED_ADD_DATA_TO_CELLS = "Cell data update failed due to permission restrictions or invalid cell references. Please verify you have edit access and the cell references are valid.";
    public static final String FAILED_CREATE_SPREADSHEET = "Spreadsheet creation failed due to insufficient Google Drive permissions or quota limitations. Please ensure you have permission to create files in Google Drive.";
    public static final String FAILED_FIND_VALUES = "Value search operation failed due to invalid search criteria or sheet access issues. Please verify the search parameters and ensure the sheet is accessible.";
    public static final String FAILED_GET_ROWS = "Row retrieval failed due to invalid range specification or access restrictions. Please verify the row range is valid and you have read permissions for the sheet.";
    public static final String FAILED_GET_SUM = "Sum calculation failed due to invalid range specification or non-numeric data. Please ensure the specified range contains numeric values and is properly formatted.";

    // Retry configuration constants
    public static final int DEFAULT_MAX_RETRY_ATTEMPTS = 4;
    public static final long DEFAULT_INITIAL_RETRY_DELAY_MS = 1000;
    public static final long DEFAULT_MAX_RETRY_DELAY_MS = 5000;

    private CatalogConstants() {
        // Private constructor to prevent instantiation
    }
}
