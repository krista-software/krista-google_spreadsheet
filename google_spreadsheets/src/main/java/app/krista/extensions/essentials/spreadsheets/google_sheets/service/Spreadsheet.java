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


import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors.GoogleProvider;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors.GoogleProviderFactory;
import app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities.DriveFile;
import app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities.ExcelSheet;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.MessageConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.SpreadsheetConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.service.impl.extractor.SheetsInformation;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.GoogleApiExecutor;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.SheetsHelper;
import com.google.api.services.sheets.v4.model.*;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static app.krista.extensions.essentials.spreadsheets.google_sheets.util.SheetsHelper.addCellDataToValueCellAddressList;
import static app.krista.extensions.essentials.spreadsheets.google_sheets.util.SheetsHelper.getCellData;


@Service
public final class Spreadsheet {
    private final GoogleProvider googleProvider;
    private final SheetsInformation sheetsInformation;

    @Inject
    public Spreadsheet(GoogleProviderFactory factory, SheetsInformation sheetsInformation) {
        this.googleProvider = factory.create();
        this.sheetsInformation = sheetsInformation;
    }

    public List<String> clearSpreadSheetByCellRange(ExcelSheet excelSheet) {
        String range = SheetsHelper.createRangeAddress(excelSheet);
        List<String> listRangeAddress = getListRangeAddress(range);
        BatchClearValuesRequest requestBody = getBatchClearValuesRequest(listRangeAddress);
        com.google.api.services.sheets.v4.Sheets sheetsService = googleProvider.getGoogleSheetClient();
        try {
            com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.BatchClear request =
                sheetsService.spreadsheets().values().batchClear(excelSheet.excelFileId, requestBody);

            BatchClearValuesResponse response = GoogleApiExecutor.executeWithRetry(
                request::execute,
                String.format("Clear cells in spreadsheet %s, range: %s", excelSheet.excelFileId, range)
            );
            return response.getClearedRanges();
        } catch (IOException exception) {
            throw new IllegalArgumentException(MessageConstants.INVALID + exception.getMessage(), exception.getCause());
        }
    }

    private List<String> getListRangeAddress(String range) {
        List<String> listRangeAddress = new ArrayList<>();
        listRangeAddress.add(range);
        return listRangeAddress;
    }

    private BatchClearValuesRequest getBatchClearValuesRequest(List<String> listRangeAddress) {
        BatchClearValuesRequest requestBody = new BatchClearValuesRequest();
        requestBody.setRanges(listRangeAddress);
        return requestBody;
    }

    public String deleteColumnsOrRowsOfSheet(ExcelSheet excelSheet, Double startIndex, Double endIndex,
                                             String dimension) {
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        if (sheetProperties == null) return MessageConstants.INVALID_SHEET_NAME;
        Request request = getRequestForDelete(startIndex, endIndex, dimension, sheetProperties.get(1));
        batchUpdate(excelSheet.excelFileId, getRequests(request));
        return dimension + " deleted successfully";
    }

    private Request getRequestForDelete(Double startIndex, Double endIndex, String dimension, Integer sheetId) {
        return new Request().setDeleteDimension(new DeleteDimensionRequest()
                .setRange(new DimensionRange()
                        .setSheetId(sheetId)
                        .setDimension(dimension)
                        .setStartIndex(SheetsHelper.convertDoubleToInteger(startIndex))
                        .setEndIndex(SheetsHelper.convertDoubleToInteger(endIndex))
                ));
    }

    public String addEmptyColumnsOrRowsToSheet(ExcelSheet excelSheet, Double startIndex, Double endIndex,
                                               String dimension) {
        List<Integer> sheetProperties = sheetsInformation.getSheetProperties(excelSheet);
        if (checkIfNull(sheetProperties)) return MessageConstants.INVALID_SHEET_NAME;
        Request request = getRequestForAdd(startIndex, endIndex, dimension, sheetProperties.get(1));
        batchUpdate(excelSheet.excelFileId, getRequests(request));
        return dimension + " added successfully";
    }

    private List<Request> getRequests(Request request) {
        List<Request> requests = new ArrayList<>();
        requests.add(request);
        return requests;
    }

    private void batchUpdate(String spreadsheetId, List<Request> requests) {
        BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();
        content.setRequests(requests);
        com.google.api.services.sheets.v4.Sheets service = googleProvider.getGoogleSheetClient();
        try {
            GoogleApiExecutor.executeWithRetry(
                () -> service.spreadsheets().batchUpdate(spreadsheetId, content).execute(),
                String.format("Batch update spreadsheet %s", spreadsheetId)
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException(MessageConstants.INVALID + exception.getMessage(), exception.getCause());
        }
    }

    private Request getRequestForAdd(Double startIndex, Double endIndex, String dimension, Integer sheetId) {
        return new Request().setInsertDimension(new InsertDimensionRequest()
                .setRange(new DimensionRange()
                        .setSheetId(sheetId)
                        .setDimension(dimension)
                        .setStartIndex(SheetsHelper.convertDoubleToInteger(startIndex))
                        .setEndIndex(SheetsHelper.convertDoubleToInteger(endIndex))
                ));
    }

    public Double appendCellDataToSheet(ExcelSheet excelSheet, List<List<String>> cellData) {
        String range = excelSheet.sheetName;
        com.google.api.services.sheets.v4.Sheets service = googleProvider.getGoogleSheetClient();
        ValueRange body = new ValueRange().setValues(getCellData(cellData));
        AppendValuesResponse result = getAppendValuesResponse(excelSheet, range, service, body);
        UpdateValuesResponse updates = result.getUpdates();
        if (updates == null || updates.getUpdatedCells() == null) {
            return 0.0;
        }
        return Double.valueOf(updates.getUpdatedCells());
    }

    private AppendValuesResponse getAppendValuesResponse(ExcelSheet excelSheet, String range, com.google.api.services.sheets.v4.Sheets service, ValueRange body) {
        try {
            return GoogleApiExecutor.executeWithRetry(
                () -> service
                        .spreadsheets()
                        .values()
                        .append(excelSheet.excelFileId, range, body)
                        .setValueInputOption(SpreadsheetConstants.VALUE_INPUT_OPTION_USER_ENTERED)
                        .execute(),
                String.format("Append cells to spreadsheet %s, sheet: %s", excelSheet.excelFileId, range)
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException(MessageConstants.INVALID + exception.getMessage(), exception.getCause());
        }
    }

    public Double addDataToSheet(ExcelSheet excelSheet, List<List<String>> cellData
    ) {
        String range = SheetsHelper.createRangeAddress(excelSheet);
        com.google.api.services.sheets.v4.Sheets service = googleProvider.getGoogleSheetClient();
        ValueRange body = new ValueRange().setValues(getCellData(cellData));
        UpdateValuesResponse result = getUpdateValuesResponse(excelSheet, range, service, body);
        return Double.valueOf(result.getUpdatedCells());
    }

    private UpdateValuesResponse getUpdateValuesResponse(ExcelSheet excelSheet, String range, com.google.api.services.sheets.v4.Sheets service, ValueRange body) {
        try {
            return GoogleApiExecutor.executeWithRetry(
                () -> service
                        .spreadsheets()
                        .values()
                        .update(excelSheet.excelFileId, range, body)
                        .setValueInputOption(SpreadsheetConstants.VALUE_INPUT_OPTION_USER_ENTERED)
                        .execute(),
                String.format("Update cells in spreadsheet %s, range: %s", excelSheet.excelFileId, range)
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException(MessageConstants.INVALID + exception.getMessage(), exception.getCause());
        }
    }

    public List<String> findValuesWithCellId(ExcelSheet excelSheet, String searchValue) {
        String range = SheetsHelper.createRangeAddress(excelSheet);
        ValueRange result = getValueRange(excelSheet, range, googleProvider.getGoogleSheetClient());
        int numRows = result.getValues() != null ? result.getValues().size() : 0;
        if (numRows < 1) {
            return Collections.emptyList();
        }
        // [A,1]
        List<String> topLefCellAddressList = SheetsHelper.parseWorksheetAddressAndGetCellIdMap(range).get(SpreadsheetConstants.TOP_LEFT_CELL);
        char column = topLefCellAddressList.get(0).charAt(0);
        int row = Integer.parseInt(topLefCellAddressList.get(1));
        String lowerCaseValue = searchValue.toLowerCase();
        return getValueCellAddressList(result.getValues(), column, row, lowerCaseValue);
    }

    private List<String> getValueCellAddressList(List<List<Object>> rowsList, char column, int row, String lowerCaseValue) {
        List<String> valuesCellAddressList = new ArrayList<>();
        for (List<Object> rowData : rowsList) {
            addCellDataToValueCellAddressList(row, lowerCaseValue, valuesCellAddressList, rowData, column);
            row++;
        }
        return valuesCellAddressList;
    }


    private ValueRange getValueRange(ExcelSheet excelSheet, String range, com.google.api.services.sheets.v4.Sheets service) {
        try {
            return GoogleApiExecutor.executeWithRetry(
                () -> service.spreadsheets().values()
                        .get(excelSheet.excelFileId, range)
                        .execute(),
                String.format("Get values from spreadsheet %s, range: %s", excelSheet.excelFileId, range)
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException(MessageConstants.INVALID + exception.getMessage(), exception.getCause());
        }
    }

    /**
     * <pre>
     * Spreadsheet will get created at My Drive (root folder of google drive).
     * After successful creation of the file it will be moved to specified folder path
     *
     * Copy file to specified folder path.
     * </pre>
     *
     * @param spreadSheetName Spreadsheet file name
     */

    public DriveFile createSpreadSheetFile(String spreadSheetName)
            throws IOException {
        if (checkIfNull(spreadSheetName) || spreadSheetName.isBlank()) return null;
        com.google.api.services.sheets.v4.Sheets service = googleProvider.getGoogleSheetClient();
        com.google.api.services.sheets.v4.model.Spreadsheet spreadsheet = new com.google.api.services.sheets.v4.model.Spreadsheet().setProperties(
                new SpreadsheetProperties().setTitle(spreadSheetName));
        spreadsheet = getSpreadsheet(service, spreadsheet);
        return new DriveFile(spreadSheetName, spreadsheet.getSpreadsheetId());
    }

    private com.google.api.services.sheets.v4.model.Spreadsheet getSpreadsheet(com.google.api.services.sheets.v4.Sheets service, com.google.api.services.sheets.v4.model.Spreadsheet spreadsheet) throws IOException {
        return GoogleApiExecutor.executeWithRetry(
            () -> service
                    .spreadsheets()
                    .create(spreadsheet)
                    .setFields(SpreadsheetConstants.SPREADSHEET_ID)
                    .execute(),
            String.format("Create spreadsheet with name: %s", spreadsheet.getProperties().getTitle())
        );
    }


    private boolean checkIfNull(Object input) {
        return input == null;
    }


}
