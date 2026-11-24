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

import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors.KristaMediaClient;
import app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities.DriveFile;
import app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities.ExcelSheet;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.MessageConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.SpreadsheetConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.service.impl.extractor.SheetsInformation;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.CSVFileHelper;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.SheetsHelper;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.ValidationHelper;
import app.krista.model.base.File;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public final class GoogleSheet {
    private final Spreadsheet spreadsheet;
    private final SheetsInformation sheetsInformation;
    private final KristaMediaClient kristaMediaClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheet.class);

    @Inject
    public GoogleSheet(Spreadsheet spreadsheet, SheetsInformation sheetsInformation, KristaMediaClient kristaMediaClient) {
        this.spreadsheet = spreadsheet;
        this.sheetsInformation = sheetsInformation;
        this.kristaMediaClient = kristaMediaClient;
    }

    public String clearSpreadsheetCells(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell) {
        validateInputParamsForRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        validateInputParamsForCreateSpreadsheet(sheetsInformation.getSheetProperties(excelSheet) == null, MessageConstants.INVALID_SHEET_NAME);
        if (spreadsheet.clearSpreadSheetByCellRange(excelSheet) == null) {
            return MessageConstants.CELL_RANGE_NOT_CLEARED;
        }
        return MessageConstants.CELL_RANGE_CLEARED;
    }

    ////

    public String deleteSheetColumns(String spreadsheetId, String sheetName, Double columnStartIndex, Double columnEndIndex) {
        validateInputParamsForDeletingSheetColumns(spreadsheetId, sheetName, ValidationHelper.isNullNegativeIndex(columnStartIndex), ValidationHelper.isNullNegativeIndex(columnEndIndex));
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName);
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        validateInputParamsForCreateSpreadsheet(sheetProperties == null, MessageConstants.INVALID_SHEET_NAME);
        return spreadsheet.deleteColumnsOrRowsOfSheet(excelSheet, columnStartIndex, columnEndIndex,
                SpreadsheetConstants.COLUMNS);
    }

    public String deleteSheetRows(String spreadsheetId, String sheetName, Double rowStartIndex, Double rowEndIndex) {
        validateInputParamsForDeletingSheetColumns(spreadsheetId, sheetName, ValidationHelper.isNullNegativeIndex(rowStartIndex), ValidationHelper.isNullNegativeIndex(rowEndIndex));
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName);
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        validateInputParamsForCreateSpreadsheet(sheetProperties == null, MessageConstants.INVALID_SHEET_NAME);
        return spreadsheet.deleteColumnsOrRowsOfSheet(excelSheet, rowStartIndex, rowEndIndex,
                SpreadsheetConstants.ROWS);
    }

    public String addEmptySheetColumns(String spreadsheetId, String sheetName, Double columnStartIndex, Double columnEndIndex) {
        validateInputParamsForDeletingSheetColumns(spreadsheetId, sheetName, ValidationHelper.isNullNegativeIndex(columnStartIndex), ValidationHelper.isNullNegativeIndex(columnEndIndex));
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName);
        return spreadsheet.addEmptyColumnsOrRowsToSheet(excelSheet, columnStartIndex, columnEndIndex,
                SpreadsheetConstants.COLUMNS);
    }

    public String addEmptySheetRows(String spreadsheetId, String sheetName, Double rowStartIndex, Double rowEndIndex) {
        validateInputParamsForDeletingSheetColumns(spreadsheetId, sheetName, ValidationHelper.isNullNegativeIndex(rowStartIndex), ValidationHelper.isNullNegativeIndex(rowEndIndex));
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName);
        return spreadsheet.addEmptyColumnsOrRowsToSheet(excelSheet, rowStartIndex, rowEndIndex,
                SpreadsheetConstants.ROWS);
    }

    public Double appendCellsOfSheet(String spreadsheetId, String sheetName, List<List<String>> cellData) {
        validateInputParamsForDeletingSheetColumns(spreadsheetId, sheetName);
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName);
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        validateInputParamsForCreateSpreadsheet(sheetProperties == null, MessageConstants.INVALID_SHEET_NAME);
        return spreadsheet.appendCellDataToSheet(excelSheet, cellData);
    }

    public Double appendRowsOfSheet(String spreadsheetId, String sheetName, List<Object> rows) {
        validateInputParamsForDeletingSheetColumns(spreadsheetId, sheetName);
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName);
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        validateInputParamsForCreateSpreadsheet(sheetProperties == null, MessageConstants.INVALID_SHEET_NAME);
        List<List<String>> converted = convertObjectRows(rows);
        spreadsheet.appendCellDataToSheet(excelSheet, converted);
        return (double) converted.size();
    }

    private List<List<String>> convertObjectRows(List<Object> rows) {
        List<List<String>> result = new ArrayList<>();
        for (Object row : rows) {
            if (row instanceof List) {
                List<?> rowList = (List<?>) row;
                List<String> rowValues = new ArrayList<>();
                for (Object cell : rowList) {
                    rowValues.add(cell != null ? cell.toString() : "");
                }
                result.add(rowValues);
            } else if (row instanceof Map) {
                Map<?, ?> rowMap = (Map<?, ?>) row;
                List<String> rowValues = new ArrayList<>();
                for (Object val : rowMap.values()) {
                    rowValues.add(val != null ? val.toString() : "");
                }
                result.add(rowValues);
            } else if (row != null) {
                List<String> singleCell = new ArrayList<>();
                singleCell.add(row.toString());
                result.add(singleCell);
            }
        }
        return result;
    }

    public Double addDataToSheetCells(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell, List<List<String>> cellData) {
        validateInputParamsForAddingDataToSheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell, ValidationHelper.isNullEmptyList(cellData));
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        return spreadsheet.addDataToSheet(excelSheet, cellData);
    }

    public List<String> findValuesInSheet(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell, String searchValue) {
        validateInputParamsForAddingDataToSheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell, ValidationHelper.isNullEmptyString(searchValue));
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        validateInputParamsForCreateSpreadsheet(sheetProperties == null, MessageConstants.INVALID_SHEET_NAME);
        return spreadsheet.findValuesWithCellId(excelSheet, searchValue);
    }

    private void validateInputParamsForAddingDataToSheetCells(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell, boolean nullEmptyList) {
        if (ValidationHelper.isNullEmptyString(spreadsheetId) || ValidationHelper.isNullEmptyString(sheetName)
                || ValidationHelper.isNullEmptyString(topLeftCell) || ValidationHelper.isNullEmptyString(bottomRightCell)
                || nullEmptyList) {
            throw new IllegalArgumentException(MessageConstants.PROVIDED_INVALID_INPUT);
        }
    }

    public List<List<String>> getSheetRowsByRange(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell) {
        validateInputParamsForRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        List<List<String>> rowsList = getRowList(sheetsInformation.getAvailableRowsByRange(excelSheet));
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        validateInputParamsForCreateSpreadsheet(sheetProperties == null, MessageConstants.INVALID_SHEET_NAME);
        return rowsList;
    }

    private List<List<String>> getRowList(List<List<Object>> availableRowsByRange) {
        List<List<String>> rows = new ArrayList<>();
        for (List<Object> row : availableRowsByRange) {
            List<String> rowEntry = new ArrayList<>();
            for (Object cellData : row) {
                rowEntry.add(cellData.toString());
            }
            rows.add(rowEntry);
        }
        return rows;
    }

    private void validateInputParamsForRange(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell) {
        validateInputParamsForDeletingSheetColumns(spreadsheetId, sheetName, ValidationHelper.isNullEmptyString(topLeftCell), ValidationHelper.isNullEmptyString(bottomRightCell));
    }

    public Double getSumBySheetRange(String spreadsheetId, String sheetName, String topLeftCell, String bottomRightCell) {
        validateInputParamsForRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        validateInputParamsForCreateSpreadsheet(sheetProperties == null, MessageConstants.INVALID_SHEET_NAME);
        return sheetsInformation.getSumByCellRange(excelSheet);
    }

    private void validateInputParamsForDeletingSheetColumns(String spreadsheetId, String sheetName, boolean nullNegativeIndex, boolean nullNegativeIndex2) {
        if (ValidationHelper.isNullEmptyString(spreadsheetId) || ValidationHelper.isNullEmptyString(sheetName)
                || nullNegativeIndex || nullNegativeIndex2) {
            throw new IllegalArgumentException(MessageConstants.PROVIDED_INVALID_INPUT);
        }
    }
    private void validateInputParamsForDeletingSheetColumns(String spreadsheetId, String sheetName) {
        if (ValidationHelper.isNullEmptyString(spreadsheetId) || ValidationHelper.isNullEmptyString(sheetName)) {
            throw new IllegalArgumentException(MessageConstants.PROVIDED_INVALID_INPUT);
        }
    }

    public DriveFile createSpreadsheet(String spreadsheetName) throws IOException {
        validateInputParamsForCreateSpreadsheet(ValidationHelper.isNullEmptyString(spreadsheetName), MessageConstants.PROVIDED_INVALID_INPUT);
        return spreadsheet.createSpreadSheetFile(spreadsheetName);
    }

    private void validateInputParamsForCreateSpreadsheet(boolean nullEmptyString, String providedInvalidInput) {
        if (nullEmptyString) {
            throw new IllegalArgumentException(providedInvalidInput);
        }
    }


    ///// Handle CSV file ////

    public String addDataRowsFromCsv(String spreadsheetId, String sheetName, File cSVFile) throws IOException {
        validateInputParamsForAddingDataRowsFromCsv(spreadsheetId, sheetName);
        java.io.File file = kristaMediaClient.toJavaFile(cSVFile);
        // rowData is list->list
        // like [ [1, 2, 3], [4, 5, 6] ]
        List<List<String>> rowData = CSVFileHelper.getRowsFromCSVFile(file);
        if (rowData.isEmpty()) {
            return MessageConstants.PROVIDED_CSV_DOES_NOT_CONTAIN_DATA;
        }
        int rowSize = rowData.size();
        int columnSize = (rowData.getFirst()).size();
        List<String> diagonalCellList = SheetsHelper.getDiagonalCellIds(rowSize, columnSize);
        try {
            ExcelSheet excelSheet = new ExcelSheet(spreadsheetId, null,
                    null, sheetName, diagonalCellList.get(0), diagonalCellList.get(1));
            Double numberOfCellsUpdated = spreadsheet.addDataToSheet(excelSheet, rowData);
            return "Data is added to sheet successfully and " + numberOfCellsUpdated + " number of cells updated";
        }catch (RuntimeException cause){
            LOGGER.error("Failed to add Data: {}", cause.getMessage(),cause);
        }
        return "Failed to add Data";
    }

    private void validateInputParamsForAddingDataRowsFromCsv(String spreadsheetId, String sheetName) {
        if (ValidationHelper.isNullEmptyString(spreadsheetId) || ValidationHelper.isNullEmptyString(sheetName)) {
            throw new IllegalArgumentException(MessageConstants.PROVIDED_INVALID_INPUT);
        }
    }
}
