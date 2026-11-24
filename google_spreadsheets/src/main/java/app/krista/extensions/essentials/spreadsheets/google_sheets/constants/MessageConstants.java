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

public final class MessageConstants {
    // Input Validation Messages - Following format: Problem + Requirements + Resolution
    public static final String PROVIDED_INVALID_INPUT = "Input validation failed because one or more required fields contain invalid data. Please verify that all required fields are filled with valid values and try again.";
    public static final String INVALID_INPUT_PARAM = "Invalid input parameters detected. Please check that all required fields are provided with correct data types and formats, then retry the operation.";

    // File and Data Messages
    public static final String PROVIDED_CSV_DOES_NOT_CONTAIN_DATA = "CSV file processing failed because the file appears to be empty or contains no valid data rows. Please ensure your CSV file contains data and has proper formatting with headers and data rows.";
    public static final String UNSUPPORTED_FILE_EXTENSION = "File upload failed due to unsupported file format. Only CSV files are supported for data import. Please convert your file to CSV format and try again.";

    // Sheet and Cell Messages
    public static final String INVALID_SHEET_NAME = "Sheet operation failed because the specified sheet name does not exist in the spreadsheet. Please verify the sheet name is correct and exists in your spreadsheet.";
    public static final String CELL_RANGE_CLEARED = "Cell range has been successfully cleared.";
    public static final String CELL_RANGE_NOT_CLEARED = "Cell range clearing failed due to insufficient permissions or invalid range specification. Please verify you have edit permissions and the cell range is valid (e.g., A1:C10).";
    public static final String CELL_IS_NOT_VALID = "Cell reference is invalid. Please use proper cell notation (e.g., A1, B5, C10) and ensure the cell exists within the sheet boundaries.";

    // Authentication and Connection Messages
    public static final String NULL_REFRESH_TOKEN = "Authentication expired and requires renewal. Your Google account authorization has expired. Please click 'Authorize' to reconnect your Google account and try again.";
    public static final String NOT_FOUND = "Resource not found. The requested spreadsheet or sheet could not be located. Please verify the spreadsheet ID and sheet name are correct.";
    public static final String CONNECTION_NOT_ESTABLISHED = "Connection failed due to invalid configuration. Please verify your Google API credentials are correctly configured and try again.";
    public static final String MISSING_ATTRIBUTES = "Required information is missing. Please ensure all mandatory fields are completed before proceeding with the operation.";
    public static final String INVALID = "Spreadsheet or sheet identification failed. Please verify the spreadsheet ID is correct and the sheet name exists in the spreadsheet.";

    private MessageConstants() {
    }
}
