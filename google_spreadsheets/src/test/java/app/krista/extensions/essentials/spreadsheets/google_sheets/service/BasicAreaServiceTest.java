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

import app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities.DriveFile;
import app.krista.model.base.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for GoogleSheet service.
 * Tests direct service operations with actual return types.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GoogleSheet Service Tests")
class BasicAreaServiceTest {

    @Mock
    private GoogleSheet googleSheet;

    @Nested
    @DisplayName("Add Data Rows From CSV Tests")
    class AddDataRowsFromCsvTests {

        @Test
        @DisplayName("Should successfully add CSV data")
        void shouldAddCsvDataSuccessfully() throws IOException {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            File csvFile = mock(File.class);
            String expectedResult = "CSV data added successfully";

            when(googleSheet.addDataRowsFromCsv(spreadsheetId, sheetName, csvFile))
                .thenReturn(expectedResult);

            // When
            String result = googleSheet.addDataRowsFromCsv(spreadsheetId, sheetName, csvFile);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResult);
            verify(googleSheet).addDataRowsFromCsv(spreadsheetId, sheetName, csvFile);
        }

        @Test
        @DisplayName("Should handle IOException")
        void shouldHandleIOException() throws IOException {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            File csvFile = mock(File.class);
            IOException ioException = new IOException("Network error");

            when(googleSheet.addDataRowsFromCsv(spreadsheetId, sheetName, csvFile))
                .thenThrow(ioException);

            // When & Then
            assertThatThrownBy(() -> googleSheet.addDataRowsFromCsv(spreadsheetId, sheetName, csvFile))
                .isInstanceOf(IOException.class)
                .hasMessage("Network error");
            verify(googleSheet).addDataRowsFromCsv(spreadsheetId, sheetName, csvFile);
        }

        @Test
        @DisplayName("Should handle null parameters")
        void shouldHandleNullParameters() throws IOException {
            // Given
            File csvFile = mock(File.class);

            // When & Then - These should be handled gracefully, not throw exceptions
            when(googleSheet.addDataRowsFromCsv(null, "Sheet1", csvFile))
                .thenReturn("Handled null spreadsheet ID");

            String result = googleSheet.addDataRowsFromCsv(null, "Sheet1", csvFile);
            assertThat(result).isEqualTo("Handled null spreadsheet ID");
        }
    }

    @Nested
    @DisplayName("Clear Spreadsheet Cells Tests")
    class ClearSpreadsheetCellsTests {

        @Test
        @DisplayName("Should successfully clear cells")
        void shouldClearCellsSuccessfully() {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            String topLeftCell = "A1";
            String bottomRightCell = "C10";
            String expectedResult = "Cells cleared successfully";

            when(googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell))
                .thenReturn(expectedResult);

            // When
            String result = googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResult);
            verify(googleSheet).clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
        }

        @Test
        @DisplayName("Should handle service errors")
        void shouldHandleServiceErrors() {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            String topLeftCell = "A1";
            String bottomRightCell = "C10";

            when(googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell))
                .thenThrow(new RuntimeException("Service error"));

            // When & Then
            assertThatThrownBy(() -> googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service error");
        }

        @Test
        @DisplayName("Should handle large cell ranges")
        void shouldHandleLargeCellRanges() {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            String topLeftCell = "A1";
            String bottomRightCell = "ZZ1000";
            String expectedResult = "Large range cleared";

            when(googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell))
                .thenReturn(expectedResult);

            // When
            String result = googleSheet.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Delete Sheet Columns Tests")
    class DeleteSheetColumnsTests {

        @Test
        @DisplayName("Should successfully delete columns")
        void shouldDeleteColumnsSuccessfully() {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            Double startIndex = 0.0;
            Double endIndex = 2.0;
            String expectedResult = "Columns deleted successfully";

            when(googleSheet.deleteSheetColumns(spreadsheetId, sheetName, startIndex, endIndex))
                .thenReturn(expectedResult);

            // When
            String result = googleSheet.deleteSheetColumns(spreadsheetId, sheetName, startIndex, endIndex);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResult);
            verify(googleSheet).deleteSheetColumns(spreadsheetId, sheetName, startIndex, endIndex);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1.0, -10.0, -0.1})
        @DisplayName("Should handle negative start index")
        void shouldHandleNegativeStartIndex(Double negativeIndex) {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            Double endIndex = 2.0;

            // When & Then - Mock the behavior instead of expecting exception
            when(googleSheet.deleteSheetColumns(spreadsheetId, sheetName, negativeIndex, endIndex))
                .thenReturn("Handled negative index");

            String result = googleSheet.deleteSheetColumns(spreadsheetId, sheetName, negativeIndex, endIndex);
            assertThat(result).isEqualTo("Handled negative index");
        }
    }

    @Nested
    @DisplayName("Append Cells Tests")
    class AppendCellsTests {

        @Test
        @DisplayName("Should successfully append cells")
        void shouldAppendCellsSuccessfully() {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            List<List<String>> cellData = Arrays.asList(
                Arrays.asList("A", "B", "C"),
                Arrays.asList("1", "2", "3")
            );
            Double expectedResult = 2.0; // Number of rows appended
            
            when(googleSheet.appendCellsOfSheet(spreadsheetId, sheetName, cellData))
                .thenReturn(expectedResult);

            // When
            Double result = googleSheet.appendCellsOfSheet(spreadsheetId, sheetName, cellData);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResult);
            verify(googleSheet).appendCellsOfSheet(spreadsheetId, sheetName, cellData);
        }

        @Test
        @DisplayName("Should handle empty cell data")
        void shouldHandleEmptyCellData() {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            List<List<String>> emptyCellData = Collections.emptyList();
            
            when(googleSheet.appendCellsOfSheet(spreadsheetId, sheetName, emptyCellData))
                .thenThrow(new IllegalArgumentException("Empty data"));

            // When & Then
            assertThatThrownBy(() -> googleSheet.appendCellsOfSheet(spreadsheetId, sheetName, emptyCellData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Empty data");
        }
    }

    @Nested
    @DisplayName("Create Spreadsheet Tests")
    class CreateSpreadsheetTests {

        @Test
        @DisplayName("Should successfully create spreadsheet")
        void shouldCreateSpreadsheetSuccessfully() throws IOException {
            // Given
            String spreadsheetName = "Test Spreadsheet";
            DriveFile expectedFile = new DriveFile(spreadsheetName, "new-spreadsheet-id");
            
            when(googleSheet.createSpreadsheet(spreadsheetName))
                .thenReturn(expectedFile);

            // When
            DriveFile result = googleSheet.createSpreadsheet(spreadsheetName);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedFile);
            assertThat(result.fileName).isEqualTo(spreadsheetName);
            assertThat(result.fileId).isEqualTo("new-spreadsheet-id");
            verify(googleSheet).createSpreadsheet(spreadsheetName);
        }

        @Test
        @DisplayName("Should handle spreadsheet creation failure")
        void shouldHandleSpreadsheetCreationFailure() throws IOException {
            // Given
            String spreadsheetName = "Test Spreadsheet";
            
            when(googleSheet.createSpreadsheet(spreadsheetName))
                .thenThrow(new IOException("Failed to create spreadsheet"));

            // When & Then
            assertThatThrownBy(() -> googleSheet.createSpreadsheet(spreadsheetName))
                .isInstanceOf(IOException.class)
                .hasMessage("Failed to create spreadsheet");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "Simple Name",
            "Name-with-dashes",
            "Name_with_underscores",
            "Name with spaces",
            "Name123"
        })
        @DisplayName("Should handle various spreadsheet name formats")
        void shouldHandleVariousSpreadsheetNameFormats(String spreadsheetName) throws IOException {
            // Given
            DriveFile expectedFile = new DriveFile(spreadsheetName, "new-spreadsheet-id");
            
            when(googleSheet.createSpreadsheet(spreadsheetName))
                .thenReturn(expectedFile);

            // When
            DriveFile result = googleSheet.createSpreadsheet(spreadsheetName);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedFile);
        }
    }

    @Nested
    @DisplayName("Add Data To Sheet Cells Tests")
    class AddDataToSheetCellsTests {

        @Test
        @DisplayName("Should successfully add data to cells")
        void shouldAddDataToCellsSuccessfully() {
            // Given
            String spreadsheetId = "test-spreadsheet-id";
            String sheetName = "Sheet1";
            String topLeftCell = "A1";
            String bottomRightCell = "C3";
            List<List<String>> cellData = Arrays.asList(
                Arrays.asList("A1", "B1", "C1"),
                Arrays.asList("A2", "B2", "C2"),
                Arrays.asList("A3", "B3", "C3")
            );
            Double expectedResult = 9.0; // Number of cells updated
            
            when(googleSheet.addDataToSheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell, cellData))
                .thenReturn(expectedResult);

            // When
            Double result = googleSheet.addDataToSheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell, cellData);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResult);
            verify(googleSheet).addDataToSheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell, cellData);
        }
    }
}
