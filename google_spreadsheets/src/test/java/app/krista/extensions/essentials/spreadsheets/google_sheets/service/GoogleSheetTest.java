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
import app.krista.extensions.essentials.spreadsheets.google_sheets.service.impl.extractor.SheetsInformation;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.CSVFileHelper;
import app.krista.model.base.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleSheetTest {

    @Mock
    private Spreadsheet spreadsheet;

    @Mock
    private SheetsInformation sheetsInformation;

    @Mock
    private KristaMediaClient kristaMediaClient;

    @Mock
    private File csvFile;

    private GoogleSheet googleSheet;

    @BeforeEach
    void setUp() {
        googleSheet = new GoogleSheet(spreadsheet, sheetsInformation, kristaMediaClient);
    }

    @Test
    void testClearSpreadsheetCells_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";
        
        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.clearSpreadSheetByCellRange(any(ExcelSheet.class))).thenReturn(Arrays.asList("A1:B2"));

        // When
        String result = googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell);

        // Then
        assertEquals(MessageConstants.CELL_RANGE_CLEARED, result);
        verify(spreadsheet).clearSpreadSheetByCellRange(any(ExcelSheet.class));
    }

    @Test
    void testClearSpreadsheetCells_InvalidSheetName() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "InvalidSheet";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";
        
        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell));
    }

    @Test
    void testClearSpreadsheetCells_InvalidInput() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.clearSpreadsheetCells(null, "Sheet1", "A1", "B2"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.clearSpreadsheetCells("id", "", "A1", "B2"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.clearSpreadsheetCells("id", "Sheet1", null, "B2"));
    }

    @Test
    void testDeleteSheetColumns_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        Double columnStartIndex = 1.0;
        Double columnEndIndex = 3.0;
        String expectedResult = "Columns deleted successfully";

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.deleteColumnsOrRowsOfSheet(any(ExcelSheet.class), eq(columnStartIndex), eq(columnEndIndex), eq("Columns")))
            .thenReturn(expectedResult);

        // When
        String result = googleSheet.deleteSheetColumns(spreadsheetId, sheetName, columnStartIndex, columnEndIndex);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void testDeleteSheetRows_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        Double rowStartIndex = 1.0;
        Double rowEndIndex = 3.0;
        String expectedResult = "Rows deleted successfully";

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.deleteColumnsOrRowsOfSheet(any(ExcelSheet.class), eq(rowStartIndex), eq(rowEndIndex), eq("Rows")))
            .thenReturn(expectedResult);

        // When
        String result = googleSheet.deleteSheetRows(spreadsheetId, sheetName, rowStartIndex, rowEndIndex);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void testAddEmptySheetColumns_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        Double columnStartIndex = 1.0;
        Double columnEndIndex = 3.0;
        String expectedResult = "Columns added successfully";

        when(spreadsheet.addEmptyColumnsOrRowsToSheet(any(ExcelSheet.class), eq(columnStartIndex), eq(columnEndIndex), eq("Columns")))
            .thenReturn(expectedResult);

        // When
        String result = googleSheet.addEmptySheetColumns(spreadsheetId, sheetName, columnStartIndex, columnEndIndex);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void testAddEmptySheetRows_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        Double rowStartIndex = 1.0;
        Double rowEndIndex = 3.0;
        String expectedResult = "Rows added successfully";

        when(spreadsheet.addEmptyColumnsOrRowsToSheet(any(ExcelSheet.class), eq(rowStartIndex), eq(rowEndIndex), eq("Rows")))
            .thenReturn(expectedResult);

        // When
        String result = googleSheet.addEmptySheetRows(spreadsheetId, sheetName, rowStartIndex, rowEndIndex);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void testAppendCellsOfSheet_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        List<List<String>> cellData = Arrays.asList(
            Arrays.asList("A", "B"),
            Arrays.asList("C", "D")
        );
        Double expectedResult = 4.0;
        
        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.appendCellDataToSheet(any(ExcelSheet.class), eq(cellData))).thenReturn(expectedResult);

        // When
        Double result = googleSheet.appendCellsOfSheet(spreadsheetId, sheetName, cellData);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void testAddDataToSheetCells_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";
        List<List<String>> cellData = Arrays.asList(
            Arrays.asList("A", "B"),
            Arrays.asList("C", "D")
        );
        Double expectedResult = 4.0;
        
        when(spreadsheet.addDataToSheet(any(ExcelSheet.class), eq(cellData))).thenReturn(expectedResult);

        // When
        Double result = googleSheet.addDataToSheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell, cellData);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindValuesInSheet_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";
        String searchValue = "test";
        List<String> expectedResult = Arrays.asList("A1", "B2");
        
        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.findValuesWithCellId(any(ExcelSheet.class), eq(searchValue))).thenReturn(expectedResult);

        // When
        List<String> result = googleSheet.findValuesInSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell, searchValue);

        // Then
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetSheetRowsByRange_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";
        List<List<Object>> mockRows = Arrays.asList(
            Arrays.asList("A", "B"),
            Arrays.asList("C", "D")
        );
        
        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(sheetsInformation.getAvailableRowsByRange(any(ExcelSheet.class))).thenReturn(mockRows);

        // When
        List<List<String>> result = googleSheet.getSheetRowsByRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell);

        // Then
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("A", "B"), result.get(0));
        assertEquals(Arrays.asList("C", "D"), result.get(1));
    }

    @Test
    void testGetSumBySheetRange_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";
        Double expectedSum = 100.0;
        
        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(sheetsInformation.getSumByCellRange(any(ExcelSheet.class))).thenReturn(expectedSum);

        // When
        Double result = googleSheet.getSumBySheetRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell);

        // Then
        assertEquals(expectedSum, result);
    }

    @Test
    void testCreateSpreadsheet_Success() throws IOException {
        // Given
        String spreadsheetName = "Test Spreadsheet";
        DriveFile expectedFile = new DriveFile(spreadsheetName, "test-id");
        
        when(spreadsheet.createSpreadSheetFile(spreadsheetName)).thenReturn(expectedFile);

        // When
        DriveFile result = googleSheet.createSpreadsheet(spreadsheetName);

        // Then
        assertEquals(expectedFile, result);
    }

    @Test
    void testCreateSpreadsheet_InvalidInput() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.createSpreadsheet(null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.createSpreadsheet(""));
    }

    @Test
    void testAddDataRowsFromCsv_Success() throws IOException {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        java.io.File mockJavaFile = mock(java.io.File.class);
        List<List<String>> csvData = Arrays.asList(
            Arrays.asList("Header1", "Header2"),
            Arrays.asList("Data1", "Data2")
        );
        
        when(kristaMediaClient.toJavaFile(csvFile)).thenReturn(mockJavaFile);
        
        try (MockedStatic<CSVFileHelper> csvHelper = mockStatic(CSVFileHelper.class)) {
            csvHelper.when(() -> CSVFileHelper.getRowsFromCSVFile(mockJavaFile)).thenReturn(csvData);
            when(spreadsheet.addDataToSheet(any(ExcelSheet.class), eq(csvData))).thenReturn(4.0);

            // When
            String result = googleSheet.addDataRowsFromCsv(spreadsheetId, sheetName, csvFile);

            // Then
            assertTrue(result.contains("Data is added to sheet successfully"));
            assertTrue(result.contains("4.0"));
        }
    }

    @Test
    void testAddDataRowsFromCsv_EmptyCSV() throws IOException {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        java.io.File mockJavaFile = mock(java.io.File.class);
        
        when(kristaMediaClient.toJavaFile(csvFile)).thenReturn(mockJavaFile);
        
        try (MockedStatic<CSVFileHelper> csvHelper = mockStatic(CSVFileHelper.class)) {
            csvHelper.when(() -> CSVFileHelper.getRowsFromCSVFile(mockJavaFile)).thenReturn(Collections.emptyList());

            // When
            String result = googleSheet.addDataRowsFromCsv(spreadsheetId, sheetName, csvFile);

            // Then
            assertEquals(MessageConstants.PROVIDED_CSV_DOES_NOT_CONTAIN_DATA, result);
        }
    }

    @Test
    void testAddDataRowsFromCsv_Exception() throws IOException {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        java.io.File mockJavaFile = mock(java.io.File.class);
        List<List<String>> csvData = Arrays.asList(
            Arrays.asList("Header1", "Header2")
        );
        
        when(kristaMediaClient.toJavaFile(csvFile)).thenReturn(mockJavaFile);
        
        try (MockedStatic<CSVFileHelper> csvHelper = mockStatic(CSVFileHelper.class)) {
            csvHelper.when(() -> CSVFileHelper.getRowsFromCSVFile(mockJavaFile)).thenReturn(csvData);
            when(spreadsheet.addDataToSheet(any(ExcelSheet.class), eq(csvData))).thenThrow(new RuntimeException("Test exception"));

            // When
            String result = googleSheet.addDataRowsFromCsv(spreadsheetId, sheetName, csvFile);

            // Then
            assertEquals("Failed to add Data", result);
        }
    }

    @Test
    void testNegativeIndexValidation() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.deleteSheetColumns("id", "Sheet1", -1.0, 3.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.deleteSheetRows("id", "Sheet1", 1.0, -1.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            googleSheet.addEmptySheetColumns("id", "Sheet1", null, 3.0));
    }

    @Test
    void testDeleteSheetColumns_InvalidSheetName() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "InvalidSheet";
        Double columnStartIndex = 1.0;
        Double columnEndIndex = 3.0;

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.deleteSheetColumns(spreadsheetId, sheetName, columnStartIndex, columnEndIndex));
    }

    @Test
    void testDeleteSheetRows_InvalidSheetName() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "InvalidSheet";
        Double rowStartIndex = 1.0;
        Double rowEndIndex = 3.0;

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.deleteSheetRows(spreadsheetId, sheetName, rowStartIndex, rowEndIndex));
    }

    @Test
    void testAppendCellsOfSheet_InvalidSheetName() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "InvalidSheet";
        List<List<String>> cellData = Arrays.asList(Arrays.asList("A", "B"));

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.appendCellsOfSheet(spreadsheetId, sheetName, cellData));
    }

    @Test
    void testFindValuesInSheet_InvalidSheetName() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "InvalidSheet";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";
        String searchValue = "test";

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.findValuesInSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell, searchValue));
    }

    @Test
    void testGetSheetRowsByRange_InvalidSheetName() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "InvalidSheet";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.getSheetRowsByRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell));
    }

    @Test
    void testGetSumBySheetRange_InvalidSheetName() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "InvalidSheet";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.getSumBySheetRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell));
    }

    @Test
    void testClearSpreadsheetCells_ClearFailed() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        String topLeftCell = "A1";
        String bottomRightCell = "B2";

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.clearSpreadSheetByCellRange(any(ExcelSheet.class))).thenReturn(null);

        // When
        String result = googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell);

        // Then
        assertEquals(MessageConstants.CELL_RANGE_NOT_CLEARED, result);
    }

    @Test
    void testAddDataRowsFromCsv_InvalidInput() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.addDataRowsFromCsv(null, "Sheet1", csvFile));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.addDataRowsFromCsv("id", "", csvFile));

        // This test expects NullPointerException because the implementation calls
        // kristaMediaClient.toJavaFile(null) which leads to CSVFileHelper.getRowsFromCSVFile(null)
        // which tries to call file.isFile() on null, causing NullPointerException
        assertThrows(NullPointerException.class, () ->
            googleSheet.addDataRowsFromCsv("id", "Sheet1", null));
    }

    @Test
    void testAllMethodsWithNullSpreadsheetId() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.deleteSheetColumns(null, "Sheet1", 1.0, 2.0));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.deleteSheetRows(null, "Sheet1", 1.0, 2.0));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.addEmptySheetColumns(null, "Sheet1", 1.0, 2.0));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.addEmptySheetRows(null, "Sheet1", 1.0, 2.0));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.appendCellsOfSheet(null, "Sheet1", Arrays.asList(Arrays.asList("A"))));
    }

    @Test
    void testAllMethodsWithEmptySheetName() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.deleteSheetColumns("id", "", 1.0, 2.0));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.deleteSheetRows("id", "", 1.0, 2.0));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.addEmptySheetColumns("id", "", 1.0, 2.0));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.addEmptySheetRows("id", "", 1.0, 2.0));
    }

    // --- appendRowsOfSheet and convertObjectRows tests ---

    @Test
    void testAppendRowsOfSheet_SuccessWithListRows() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        List<Object> rows = Arrays.asList(
            Arrays.asList("A", "B", "C"),
            Arrays.asList("1", "2", "3")
        );

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.appendCellDataToSheet(any(ExcelSheet.class), anyList())).thenReturn(6.0);

        // When
        Double result = googleSheet.appendRowsOfSheet(spreadsheetId, sheetName, rows);

        // Then
        assertEquals(2.0, result);
        verify(spreadsheet).appendCellDataToSheet(any(ExcelSheet.class), eq(Arrays.asList(
            Arrays.asList("A", "B", "C"),
            Arrays.asList("1", "2", "3")
        )));
    }

    @Test
    void testAppendRowsOfSheet_SuccessWithMapRows() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("col1", "val1");
        row1.put("col2", "val2");
        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("col1", "val3");
        row2.put("col2", "val4");
        List<Object> rows = Arrays.asList(row1, row2);

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.appendCellDataToSheet(any(ExcelSheet.class), anyList())).thenReturn(4.0);

        // When
        Double result = googleSheet.appendRowsOfSheet(spreadsheetId, sheetName, rows);

        // Then
        assertEquals(2.0, result);
        verify(spreadsheet).appendCellDataToSheet(any(ExcelSheet.class), eq(Arrays.asList(
            Arrays.asList("val1", "val2"),
            Arrays.asList("val3", "val4")
        )));
    }

    @Test
    void testAppendRowsOfSheet_SuccessWithStringRows() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        List<Object> rows = Arrays.asList("singleValue1", "singleValue2", "singleValue3");

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.appendCellDataToSheet(any(ExcelSheet.class), anyList())).thenReturn(3.0);

        // When
        Double result = googleSheet.appendRowsOfSheet(spreadsheetId, sheetName, rows);

        // Then
        assertEquals(3.0, result);
        verify(spreadsheet).appendCellDataToSheet(any(ExcelSheet.class), eq(Arrays.asList(
            Collections.singletonList("singleValue1"),
            Collections.singletonList("singleValue2"),
            Collections.singletonList("singleValue3")
        )));
    }

    @Test
    void testAppendRowsOfSheet_MixedRowTypes() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        Map<String, Object> mapRow = new LinkedHashMap<>();
        mapRow.put("key", "mapVal");
        List<Object> rows = Arrays.asList(
            Arrays.asList("listVal1", "listVal2"),
            mapRow,
            "stringVal"
        );

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.appendCellDataToSheet(any(ExcelSheet.class), anyList())).thenReturn(4.0);

        // When
        Double result = googleSheet.appendRowsOfSheet(spreadsheetId, sheetName, rows);

        // Then
        assertEquals(3.0, result);
    }

    @Test
    void testAppendRowsOfSheet_WithNullElementsInRows() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        List<Object> rows = Arrays.asList(
            Arrays.asList("A", null, "C"),
            null
        );

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.appendCellDataToSheet(any(ExcelSheet.class), anyList())).thenReturn(3.0);

        // When
        Double result = googleSheet.appendRowsOfSheet(spreadsheetId, sheetName, rows);

        // Then - null row is skipped, only 1 row converted
        assertEquals(1.0, result);
        verify(spreadsheet).appendCellDataToSheet(any(ExcelSheet.class), eq(Arrays.asList(
            Arrays.asList("A", "", "C")
        )));
    }

    @Test
    void testAppendRowsOfSheet_WithNullValuesInMap() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        Map<String, Object> mapRow = new LinkedHashMap<>();
        mapRow.put("col1", "val1");
        mapRow.put("col2", null);
        List<Object> rows = Collections.singletonList(mapRow);

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.appendCellDataToSheet(any(ExcelSheet.class), anyList())).thenReturn(2.0);

        // When
        Double result = googleSheet.appendRowsOfSheet(spreadsheetId, sheetName, rows);

        // Then
        assertEquals(1.0, result);
        verify(spreadsheet).appendCellDataToSheet(any(ExcelSheet.class), eq(Arrays.asList(
            Arrays.asList("val1", "")
        )));
    }

    @Test
    void testAppendRowsOfSheet_EmptyRows() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        List<Object> rows = Collections.emptyList();

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(Arrays.asList(1, 2, 3));
        when(spreadsheet.appendCellDataToSheet(any(ExcelSheet.class), anyList())).thenReturn(0.0);

        // When
        Double result = googleSheet.appendRowsOfSheet(spreadsheetId, sheetName, rows);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void testAppendRowsOfSheet_InvalidSpreadsheetId() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.appendRowsOfSheet(null, "Sheet1", Arrays.asList("data")));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.appendRowsOfSheet("", "Sheet1", Arrays.asList("data")));
    }

    @Test
    void testAppendRowsOfSheet_InvalidSheetName() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.appendRowsOfSheet("id", null, Arrays.asList("data")));

        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.appendRowsOfSheet("id", "", Arrays.asList("data")));
    }

    @Test
    void testAppendRowsOfSheet_InvalidSheetNameFromProperties() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "NonExistentSheet";
        List<Object> rows = Arrays.asList("data");

        when(sheetsInformation.getSheetProperties(any(ExcelSheet.class))).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            googleSheet.appendRowsOfSheet(spreadsheetId, sheetName, rows));
    }
}
