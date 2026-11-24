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

package app.krista.extensions.essentials.spreadsheets.google_sheets.util;


import app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities.ExcelSheet;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.MessageConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.QueryConstants;
import app.krista.extensions.essentials.spreadsheets.google_sheets.constants.SpreadsheetConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SheetsHelper {

    private SheetsHelper() {
    }


    public static List<List<Object>> getCellData(List<List<String>> cellsData) {
        List<List<Object>> cells = new ArrayList<>();
        for (List<String> cellData : cellsData) {
            List<Object> cellDataConverted = new ArrayList<>(cellData);
            cells.add(cellDataConverted);
        }
        return cells;
    }

    public static Integer convertDoubleToInteger(Double value) {
        return (int) (double) value;
    }

    /**
     * <pre>
     * It parse the worksheet address in such way,
     * for each diagonal index it will create new map entry.
     * Every map entry contains list consist of its cell row index & cell column index
     *
     * Example:
     * For sheet address 'Sheet2!A1:D5'
     * Map -> TOP_LEFT_CELL, List -> [A,1]
     *        BOTTOM_RIGHT_CELL, List -> [D,5]
     * </pre>
     *
     * @param sheetAddressRange Parsing worksheet range into the map of lists
     * @return Cell address map as shown in example
     */
    public static Map<String, List<String>> parseWorksheetAddressAndGetCellIdMap(String sheetAddressRange) {
        if (ValidationHelper.isNullEmptyString(sheetAddressRange)) {
            throw new IllegalArgumentException(MessageConstants.INVALID_INPUT_PARAM);
        }
        // from Sheet2!A1:D5 we get A1:D5
        String[] addressValues = sheetAddressRange.split(QueryConstants.EXCLAMATION);
        // from A1:D5 we get [A1,D5]
        String[] cellAddressArray = addressValues[1].split(QueryConstants.COLON);
        Map<String, String> rowColumnValue1 = getRowAndColumnValue(cellAddressArray[0]);
        assert rowColumnValue1 != null;
        String row1 = rowColumnValue1.get(SpreadsheetConstants.ROW); //1
        String column1 = rowColumnValue1.get(SpreadsheetConstants.COLUMN); //A
        Map<String, String> rowColumnValue2 = getRowAndColumnValue(cellAddressArray[1]);
        assert rowColumnValue2 != null;
        String row2 = rowColumnValue2.get(SpreadsheetConstants.ROW); //5
        String column2 = rowColumnValue2.get(SpreadsheetConstants.COLUMN); //D
        List<String> topLeftCellList = addRowAndColumnToCellList(row1, column1);
        List<String> bottomRightCell = addRowAndColumnToCellList(row2, column2);
        return fillCellAddressMap(topLeftCellList, bottomRightCell);
    }

    private static Map<String, List<String>> fillCellAddressMap(List<String> topLeftCellList, List<String> bottomRightCell) {
        Map<String, List<String>> cellAddressMap = new HashMap<>();
        cellAddressMap.put(SpreadsheetConstants.TOP_LEFT_CELL, topLeftCellList);
        cellAddressMap.put(SpreadsheetConstants.BOTTOM_RIGHT_CELL, bottomRightCell);
        return cellAddressMap;
    }

    private static List<String> addRowAndColumnToCellList(String row1, String column1) {
        List<String> cellList = new ArrayList<>();
        cellList.add(column1);
        cellList.add(row1);
        return cellList;
    }

    private static Map<String, String> getRowAndColumnValue(String cellAddress) {
        if (ValidationHelper.isNullEmptyString(cellAddress)) {
            return null;
        }
        StringBuilder rowValue = new StringBuilder();
        StringBuilder columnValue = new StringBuilder();
        Map<String, String> rowColumnValueMap = new HashMap<>();
        int i = 0;
        while (i < cellAddress.length()) {
            char ch = cellAddress.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                columnValue.append(ch);
            } else {
                rowValue.append(ch);
            }
            i++;
        }
        rowColumnValueMap.put(SpreadsheetConstants.ROW, rowValue.toString());
        rowColumnValueMap.put(SpreadsheetConstants.COLUMN, columnValue.toString());
        return rowColumnValueMap;
    }

    public static void addCellDataToValueCellAddressList(int row, String lowerCaseValue, List<String> valuesCellAddressList, List<Object> rowData, char columnName) {
        for (Object cellData : rowData) {
            if (cellData.toString().toLowerCase().equals(lowerCaseValue)) {
                valuesCellAddressList.add(String.valueOf(columnName) + row);
            }
            columnName++;
        }
    }


    /**
     * <pre>
     * Create worksheet address.
     * Worksheet address indicate the range of utilized cells
     * Example:
     *  "Sheet1!A1:D5"
     *
     * </pre>
     *
     * @param excelSheet Worksheet details
     * @return worksheet address
     */
    public static String createRangeAddress(ExcelSheet excelSheet) {
        if (excelSheet == null) {
            throw new IllegalArgumentException(MessageConstants.INVALID_INPUT_PARAM);
        }
        if (!(isValidCellId(excelSheet.topLeftCellId) && isValidCellId(excelSheet.bottomRightCellId))) {
            throw new IllegalArgumentException(MessageConstants.INVALID_INPUT_PARAM + MessageConstants.CELL_IS_NOT_VALID);
        }
        //"Sheet1!A1:D5"
        return excelSheet.sheetName + QueryConstants.EXCLAMATION
                + excelSheet.topLeftCellId
                + QueryConstants.COLON + excelSheet.bottomRightCellId;
    }

    /**
     * <pre>
     * Validate the worksheet cell id.
     * </pre>
     *
     * @param cellIndex cell index that need to validate. ex: A1
     * @return true, if string starts with alphabets and ends with numbers, otherwise false
     */
    protected static boolean isValidCellId(String cellIndex) {
        if (ValidationHelper.isNullEmptyString(cellIndex)) {
            throw new IllegalArgumentException(MessageConstants.INVALID_INPUT_PARAM);
        }
        return cellIndex.toUpperCase().matches("^(?<column>[A-Z]+)(?<row>[1-9]\\d*)$");
    }


    /**
     * <pre>
     * Generate the diagonal cell Ids using row and column size
     * </pre>
     *
     * @param row    Row size
     * @param column Column size
     * @return List of diagonal cell ids
     */
    public static List<String> getDiagonalCellIds(int row, int column) {
        if (row <= 0 || column <= 0) {
            return null;
        }
        int topLeftCell = 'A';
        int bottomRightCell = topLeftCell + column;
        String topLeftCellId = String.valueOf((char) topLeftCell) + 1;
        String bottomLeftCellId = String.valueOf((char) (bottomRightCell - 1)) + row;
        return addRowAndColumnToCellList(bottomLeftCellId, topLeftCellId);
    }
}
