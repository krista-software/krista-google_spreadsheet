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
import app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities.ExcelSheet;
import app.krista.extensions.essentials.spreadsheets.google_sheets.service.impl.extractor.SheetsInformation;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.GoogleApiExecutor;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.SheetsHelper;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Spreadsheet appendCellDataToSheet Tests")
class SpreadsheetTest {

    @Mock
    private GoogleProviderFactory googleProviderFactory;

    @Mock
    private GoogleProvider googleProvider;

    @Mock
    private SheetsInformation sheetsInformation;

    @Mock
    private com.google.api.services.sheets.v4.Sheets sheetsService;

    private Spreadsheet spreadsheet;

    @BeforeEach
    void setUp() {
        when(googleProviderFactory.create()).thenReturn(googleProvider);
        spreadsheet = new Spreadsheet(googleProviderFactory, sheetsInformation);
    }

    @Test
    @DisplayName("Should return updated cells count on success")
    void appendCellDataToSheet_Success() throws IOException {
        // Given
        ExcelSheet excelSheet = new ExcelSheet("spreadsheet-id", "Sheet1");
        List<List<String>> cellData = Arrays.asList(
            Arrays.asList("A", "B"),
            Arrays.asList("1", "2")
        );

        UpdateValuesResponse updates = new UpdateValuesResponse();
        updates.setUpdatedCells(4);

        AppendValuesResponse appendResponse = new AppendValuesResponse();
        appendResponse.setUpdates(updates);

        when(googleProvider.getGoogleSheetClient()).thenReturn(sheetsService);

        try (MockedStatic<SheetsHelper> sheetsHelperMock = mockStatic(SheetsHelper.class, CALLS_REAL_METHODS);
             MockedStatic<GoogleApiExecutor> executorMock = mockStatic(GoogleApiExecutor.class)) {

            executorMock.when(() -> GoogleApiExecutor.executeWithRetry(any(), anyString()))
                .thenReturn(appendResponse);

            // When
            Double result = spreadsheet.appendCellDataToSheet(excelSheet, cellData);

            // Then
            assertEquals(4.0, result);
        }
    }

    @Test
    @DisplayName("Should return 0.0 when updates is null")
    void appendCellDataToSheet_UpdatesNull() throws IOException {
        // Given
        ExcelSheet excelSheet = new ExcelSheet("spreadsheet-id", "Sheet1");
        List<List<String>> cellData = Arrays.asList(
            Arrays.asList("A", "B")
        );

        AppendValuesResponse appendResponse = new AppendValuesResponse();
        // updates not set → getUpdates() returns null

        when(googleProvider.getGoogleSheetClient()).thenReturn(sheetsService);

        try (MockedStatic<SheetsHelper> sheetsHelperMock = mockStatic(SheetsHelper.class, CALLS_REAL_METHODS);
             MockedStatic<GoogleApiExecutor> executorMock = mockStatic(GoogleApiExecutor.class)) {

            executorMock.when(() -> GoogleApiExecutor.executeWithRetry(any(), anyString()))
                .thenReturn(appendResponse);

            // When
            Double result = spreadsheet.appendCellDataToSheet(excelSheet, cellData);

            // Then
            assertEquals(0.0, result);
        }
    }

    @Test
    @DisplayName("Should return 0.0 when updatedCells is null")
    void appendCellDataToSheet_UpdatedCellsNull() throws IOException {
        // Given
        ExcelSheet excelSheet = new ExcelSheet("spreadsheet-id", "Sheet1");
        List<List<String>> cellData = Arrays.asList(
            Arrays.asList("A", "B")
        );

        UpdateValuesResponse updates = new UpdateValuesResponse();
        // updatedCells not set → getUpdatedCells() returns null

        AppendValuesResponse appendResponse = new AppendValuesResponse();
        appendResponse.setUpdates(updates);

        when(googleProvider.getGoogleSheetClient()).thenReturn(sheetsService);

        try (MockedStatic<SheetsHelper> sheetsHelperMock = mockStatic(SheetsHelper.class, CALLS_REAL_METHODS);
             MockedStatic<GoogleApiExecutor> executorMock = mockStatic(GoogleApiExecutor.class)) {

            executorMock.when(() -> GoogleApiExecutor.executeWithRetry(any(), anyString()))
                .thenReturn(appendResponse);

            // When
            Double result = spreadsheet.appendCellDataToSheet(excelSheet, cellData);

            // Then
            assertEquals(0.0, result);
        }
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when API throws IOException")
    void appendCellDataToSheet_IOException() throws IOException {
        // Given
        ExcelSheet excelSheet = new ExcelSheet("spreadsheet-id", "Sheet1");
        List<List<String>> cellData = Arrays.asList(
            Arrays.asList("A", "B")
        );

        when(googleProvider.getGoogleSheetClient()).thenReturn(sheetsService);

        try (MockedStatic<SheetsHelper> sheetsHelperMock = mockStatic(SheetsHelper.class, CALLS_REAL_METHODS);
             MockedStatic<GoogleApiExecutor> executorMock = mockStatic(GoogleApiExecutor.class)) {

            executorMock.when(() -> GoogleApiExecutor.executeWithRetry(any(), anyString()))
                .thenThrow(new IOException("API quota exceeded"));

            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                spreadsheet.appendCellDataToSheet(excelSheet, cellData));
        }
    }
}
