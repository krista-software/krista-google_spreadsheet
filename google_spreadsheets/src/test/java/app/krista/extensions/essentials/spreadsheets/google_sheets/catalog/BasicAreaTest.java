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

package app.krista.extensions.essentials.spreadsheets.google_sheets.catalog;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extensions.essentials.spreadsheets.google_sheets.service.BasicAreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicArea Catalog Tests")
class BasicAreaTest {

    @Mock
    private BasicAreaService basicAreaService;

    @Mock
    private ExtensionResponse mockResponse;

    private BasicArea basicArea;

    @BeforeEach
    void setUp() {
        basicArea = new BasicArea(basicAreaService);
    }

    @Test
    @DisplayName("Should append rows and return success response")
    void appendRowsAtTheEndOfSpreadsheet_Success() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        List<Object> rows = Arrays.asList(
            Arrays.asList("A", "B"),
            Arrays.asList("1", "2")
        );

        when(basicAreaService.appendRowsOfSheet(spreadsheetId, sheetName, rows))
            .thenReturn(mockResponse);

        // When
        ExtensionResponse result = basicArea.appendRowsAtTheEndOfSpreadsheet(spreadsheetId, sheetName, rows);

        // Then
        assertNotNull(result);
        assertSame(mockResponse, result);
        verify(basicAreaService).appendRowsOfSheet(spreadsheetId, sheetName, rows);
    }

    @Test
    @DisplayName("Should delegate with empty rows list")
    void appendRowsAtTheEndOfSpreadsheet_EmptyRows() {
        // Given
        String spreadsheetId = "test-spreadsheet-id";
        String sheetName = "Sheet1";
        List<Object> rows = Collections.emptyList();

        when(basicAreaService.appendRowsOfSheet(spreadsheetId, sheetName, rows))
            .thenReturn(mockResponse);

        // When
        ExtensionResponse result = basicArea.appendRowsAtTheEndOfSpreadsheet(spreadsheetId, sheetName, rows);

        // Then
        assertNotNull(result);
        verify(basicAreaService).appendRowsOfSheet(spreadsheetId, sheetName, rows);
    }

    @Test
    @DisplayName("Should delegate with null parameters")
    void appendRowsAtTheEndOfSpreadsheet_NullParams() {
        // Given
        when(basicAreaService.appendRowsOfSheet(null, null, null))
            .thenReturn(mockResponse);

        // When
        ExtensionResponse result = basicArea.appendRowsAtTheEndOfSpreadsheet(null, null, null);

        // Then
        assertNotNull(result);
        verify(basicAreaService).appendRowsOfSheet(null, null, null);
    }
}
