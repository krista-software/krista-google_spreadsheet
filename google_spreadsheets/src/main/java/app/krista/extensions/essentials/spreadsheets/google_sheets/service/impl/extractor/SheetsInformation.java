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

package app.krista.extensions.essentials.spreadsheets.google_sheets.service.impl.extractor;

import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors.GoogleProvider;
import app.krista.extensions.essentials.spreadsheets.google_sheets.authentication.connectors.GoogleProviderFactory;
import app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities.ExcelSheet;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.MessageConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.GoogleApiExecutor;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.SheetsHelper;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.ValidationHelper;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

@Service
public final class SheetsInformation {
    private static final Logger LOGGER = LoggerFactory.getLogger(SheetsInformation.class);

    private final GoogleProvider googleProvider;

    @Inject
    public SheetsInformation(GoogleProviderFactory appService) {
        this.googleProvider = appService.create();
    }


    /**
     * <pre>
     * Retrieve the data from sheet.
     * Data will be retrieved for given cell range.
     * </pre>
     *
     * @param excelSheet Spreadsheet file object contains file id and range information
     * @return List contains the data
     */
    public List<List<Object>> getAvailableRowsByRange(ExcelSheet excelSheet) {
        if (excelSheet == null) {
            LOGGER.warn("ExcelSheet is Null");
            return Collections.emptyList();
        }
        String range = SheetsHelper.createRangeAddress(excelSheet);
        ValueRange result = getValueRange(excelSheet, range);
        return result.getValues();
    }


    /**
     * <pre>
     * Get sum of numeric values available inside given range of spreadsheet.
     * </pre>
     *
     * @param excelSheet Spreadsheet file object contains file id and range information
     * @return Summation
     */
    public Double getSumByCellRange(ExcelSheet excelSheet) {
        String range = SheetsHelper.createRangeAddress(excelSheet);
        ValueRange result = getValueRange(excelSheet, range);
        List<List<Object>> row = result.getValues();
        return getTotal(row, 0L);
    }

    private double getTotal(List<List<Object>> row, double total) {
        for (List<Object> objects : row) {
            for (Object object : objects) {
                try {
                    total += Double.parseDouble((String) object);
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return total;
    }


    private ValueRange getValueRange(ExcelSheet excelSheet, String range) {
        try {
            return GoogleApiExecutor.executeWithRetry(
                () -> googleProvider.getGoogleSheetClient().spreadsheets().values()
                        .get(excelSheet.excelFileId, range)
                        .execute(),
                String.format("Get values from spreadsheet %s, range: %s", excelSheet.excelFileId, range)
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException(MessageConstants.INVALID + exception.getMessage(), exception.getCause());
        }
    }


    /**
     * Retrieving the necessary properties of the sheet.
     * Properties will be would be available in below sequence of list index.
     * 0 - Sheet index
     * 1 - Sheet id
     *
     * @param excelSheet Spreadsheet on which operation need to perform
     * @return List of properties. null if sheet name is wrong
     */
    public List<Integer> getSheetProperties(ExcelSheet excelSheet) {
        Map<String, List<Integer>> sheetNameIndexMap = getSpreadsheetFileSheetPropertyMap(
                excelSheet.excelFileId);
        return sheetNameIndexMap.get(excelSheet.sheetName);
    }

    /**
     * <pre>
     * Retrieve the all the sheets of spreadsheet.
     * Some basic sheet related information also retrieved.
     *
     * Properties will be would be available in below sequence of list index.
     * 0 - Sheet index
     * 1 - Sheet id
     * </pre>
     *
     * @param spreadsheetId Spreadsheet on which operation need to perform
     * @return Map<String, List> Sheet name & sheet properties list
     */
    private Map<String, List<Integer>> getSpreadsheetFileSheetPropertyMap(String spreadsheetId) {
        if (ValidationHelper.isNullEmptyString(spreadsheetId)) {
            throw new IllegalArgumentException(MessageConstants.INVALID_INPUT_PARAM);
        }
        com.google.api.services.sheets.v4.Sheets service = googleProvider.getGoogleSheetClient();
        try {
            List<Sheet> sheetList = GoogleApiExecutor.executeWithRetry(
                () -> service.spreadsheets().get(spreadsheetId).execute().getSheets(),
                String.format("Get spreadsheet properties for spreadsheet %s", spreadsheetId)
            );
            return getSheetPropertyMap(sheetList, new LinkedHashMap<>());
        } catch (IOException exception) {
            throw new IllegalArgumentException(MessageConstants.INVALID + exception.getMessage(), exception.getCause());
        }
    }

    private Map<String, List<Integer>> getSheetPropertyMap(List<Sheet> sheetList, Map<String, List<Integer>> sheetPropertyMap) {
        for (Sheet sheet : sheetList) {
            // First entry will be index of sheet
            // Second entry will be id of sheet
            List<Integer> sheetPropertiesList = new ArrayList<>();
            sheetPropertiesList.add(sheet.getProperties().getIndex());
            sheetPropertiesList.add(sheet.getProperties().getSheetId());
            sheetPropertyMap.put(sheet.getProperties().getTitle(), sheetPropertiesList);
        }
        return sheetPropertyMap;
    }

}
