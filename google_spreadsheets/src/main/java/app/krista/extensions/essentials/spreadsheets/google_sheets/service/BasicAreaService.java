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

package app.krista.extensions.essentials.spreadsheets.google_sheets.service;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.ErrorMessageFormatter;
import app.krista.model.base.File;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static app.krista.extensions.essentials.spreadsheets.google_sheets.constants.CatalogConstants.*;

@Service
public class BasicAreaService {

    private static final Logger logger = LoggerFactory.getLogger(BasicAreaService.class);
    private final GoogleSheet googleSheetService;

    @Inject
    public BasicAreaService(GoogleSheet googleSheetService) {
        this.googleSheetService = googleSheetService;
    }

    /**
     * Helper method to execute operations and handle exceptions consistently.
     * Retry logic and error formatting are handled at the Google API level by GoogleApiExecutor.
     */
    private <T> ExtensionResponse executeOperation(
            OperationSupplier<T> operation,
            String resultKey,
            String errorMessage,
            String logContext) {
        try {
            T result = operation.get();
            return new ExtensionResponseBuilder().success(Map.of(resultKey, result)).build();
        } catch (IllegalArgumentException cause) {
            // Extract user-friendly message from the exception
            String userMessage = extractUserFriendlyMessage(cause);
            logger.error("{} - Validation error: {}", logContext, userMessage);
            return new ExtensionResponseBuilder().failure(userMessage).build();
        } catch (Exception cause) {
            // Extract user-friendly message from the exception
            String userMessage = extractUserFriendlyMessage(cause);
            logger.error("{} - Error: {}", logContext, userMessage);
            return new ExtensionResponseBuilder().failure(userMessage).build();
        }
    }

    /**
     * Extract user-friendly message from exception.
     * The GoogleApiExecutor already wraps exceptions with user-friendly messages.
     */
    private String extractUserFriendlyMessage(Exception exception) {
        String message = exception.getMessage();

        if (message == null || message.isEmpty()) {
            return "An unexpected error occurred. Please try again.";
        }

        // Remove technical prefixes that might have been added
        message = message.replace("Invalid spreadsheet id or invalid sheet name.", "").trim();

        // If message is now empty after cleanup, provide a default
        if (message.isEmpty()) {
            return "Invalid input. Please check your spreadsheet ID and sheet name.";
        }

        return message;
    }

    /**
     * Functional interface for operations that can throw exceptions
     */
    @FunctionalInterface
    private interface OperationSupplier<T> {
        T get() throws Exception;
    }

    public ExtensionResponse addDataRowsFromCsv(String spreadsheetId, String sheetName, File cSVFile) {
        return executeOperation(
            () -> googleSheetService.addDataRowsFromCsv(spreadsheetId, sheetName, cSVFile),
            ADD_CSV_DATA_RESULT,
            FAILED_CSV_DATA,
            String.format("Failed to add CSV data to spreadsheet ID: %s, sheet: %s", spreadsheetId, sheetName)
        );
    }

    public ExtensionResponse clearSpreadsheetCells(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell) {
        return executeOperation(
            () -> googleSheetService.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell),
            CLEAR_CELLS_RESULT,
            FAILED_CLEAR_CELLS,
            String.format("Failed to clear cells in spreadsheet ID: %s, sheet: %s, range: %s:%s", spreadsheetId, sheetName, topLeftCell, bottomRightCell)
        );
    }

    public ExtensionResponse deleteSheetColumns(String spreadsheetId, String sheetName, Double columnStartIndex, Double columnEndIndex) {
        return executeOperation(
            () -> googleSheetService.deleteSheetColumns(spreadsheetId, sheetName, columnStartIndex, columnEndIndex),
            DELETE_COLUMNS_RESULT,
            FAILED_DELETE_COLUMNS,
            String.format("Failed to delete columns in spreadsheet ID: %s, sheet: %s, columns: %s-%s", spreadsheetId, sheetName, columnStartIndex, columnEndIndex)
        );
    }

    public ExtensionResponse deleteSheetRows(String spreadsheetId, String sheetName, Double rowStartIndex, Double rowEndIndex) {
        return executeOperation(
            () -> googleSheetService.deleteSheetRows(spreadsheetId, sheetName, rowStartIndex, rowEndIndex),
            DELETE_ROWS_RESULT,
            FAILED_DELETE_ROWS,
            String.format("Failed to delete rows in spreadsheet ID: %s, sheet: %s, rows: %s-%s", spreadsheetId, sheetName, rowStartIndex, rowEndIndex)
        );
    }

    public ExtensionResponse addEmptySheetColumns(String spreadsheetId, String sheetName, Double columnStartIndex, Double columnEndIndex) {
        return executeOperation(
            () -> googleSheetService.addEmptySheetColumns(spreadsheetId, sheetName, columnStartIndex, columnEndIndex),
            ADD_EMPTY_COLUMNS_RESULT,
            FAILED_ADD_EMPTY_COLUMNS,
            String.format("Failed to add empty columns in spreadsheet ID: %s, sheet: %s, columns: %s-%s", spreadsheetId, sheetName, columnStartIndex, columnEndIndex)
        );
    }

    public ExtensionResponse addEmptySheetRows(String spreadsheetId, String sheetName, Double rowStartIndex, Double rowEndIndex) {
        return executeOperation(
            () -> googleSheetService.addEmptySheetRows(spreadsheetId, sheetName, rowStartIndex, rowEndIndex),
            ADD_EMPTY_ROWS_RESULT,
            FAILED_ADD_EMPTY_ROWS,
            String.format("Failed to add empty rows in spreadsheet ID: %s, sheet: %s, rows: %s-%s", spreadsheetId, sheetName, rowStartIndex, rowEndIndex)
        );
    }

    public ExtensionResponse appendCellsOfSheet(String spreadsheetId, String sheetName, List<List<String>> cellData) {
        return executeOperation(
            () -> googleSheetService.appendCellsOfSheet(spreadsheetId, sheetName, cellData),
            APPEND_CELLS_RESULT,
            FAILED_APPEND_CELLS,
            String.format("Failed to append cells to spreadsheet ID: %s, sheet: %s", spreadsheetId, sheetName)
        );
    }

    public ExtensionResponse addDataToSheetCells(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell, List<List<String>> cellData) {
        return executeOperation(
            () -> googleSheetService.addDataToSheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell, cellData),
            ADD_DATA_TO_CELLS_RESULT,
            FAILED_ADD_DATA_TO_CELLS,
            String.format("Failed to add data to cells in spreadsheet ID: %s, sheet: %s, range: %s:%s", spreadsheetId, sheetName, topLeftCell, bottomRightCell)
        );
    }

    public ExtensionResponse createSpreadsheet(String spreadsheetName) {
        return executeOperation(
            () -> googleSheetService.createSpreadsheet(spreadsheetName),
            DRIVE_FILE_RESULT,
            FAILED_CREATE_SPREADSHEET,
            String.format("Failed to create spreadsheet with name: %s", spreadsheetName)
        );
    }

    public ExtensionResponse findValuesInSheet(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell, String searchValue) {
        return executeOperation(
            () -> googleSheetService.findValuesInSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell, searchValue),
            FIND_VALUES_RESULT,
            FAILED_FIND_VALUES,
            String.format("Failed to find values in spreadsheet ID: %s, sheet: %s, range: %s:%s, search: %s", spreadsheetId, sheetName, topLeftCell, bottomRightCell, searchValue)
        );
    }

    public ExtensionResponse getSheetRowsByRange(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell) {
        return executeOperation(
            () -> googleSheetService.getSheetRowsByRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell),
            SHEET_ROWS_RESULT,
            FAILED_GET_ROWS,
            String.format("Failed to get rows from spreadsheet ID: %s, sheet: %s, range: %s:%s", spreadsheetId, sheetName, topLeftCell, bottomRightCell)
        );
    }

    public ExtensionResponse getSumBySheetRange(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell) {
        return executeOperation(
            () -> googleSheetService.getSumBySheetRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell),
            SUM_BY_RANGE_RESULT,
            FAILED_GET_SUM,
            String.format("Failed to get sum from spreadsheet ID: %s, sheet: %s, range: %s:%s", spreadsheetId, sheetName, topLeftCell, bottomRightCell)
        );
    }


}
