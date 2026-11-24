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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SheetsHelperTest {

    @Test
    void getCellData_convertsStringRowsToObjectRows() {
        List<List<Object>> cells = SheetsHelper.getCellData(List.of(List.of("a", "b"), List.of("c")));
        assertEquals(2, cells.size());
        assertEquals("a", cells.get(0).get(0));
    }

    @Test
    void convertDoubleToInteger_truncates() {
        assertEquals(5, SheetsHelper.convertDoubleToInteger(5.9));
        assertEquals(0, SheetsHelper.convertDoubleToInteger(0.0));
    }

    @Test
    void parseWorksheetAddress_returnsTopLeftAndBottomRight() {
        Map<String, List<String>> map = SheetsHelper.parseWorksheetAddressAndGetCellIdMap("Sheet2!A1:D5");
        assertNotNull(map);
        assertEquals(2, map.size());
        for (List<String> cell : map.values()) {
            assertEquals(2, cell.size()); // [column, row]
        }
    }

    @Test
    void parseWorksheetAddress_blankInput_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> SheetsHelper.parseWorksheetAddressAndGetCellIdMap(""));
    }

    @Test
    void addCellDataToValueCellAddressList_addsMatchingAddresses() {
        List<String> found = new ArrayList<>();
        List<Object> rowData = List.of("target", "other", "target");
        SheetsHelper.addCellDataToValueCellAddressList(3, "target", found, rowData, 'A');
        assertTrue(found.contains("A3")); // first column matched
        assertTrue(found.contains("C3")); // third column matched
    }

    @Test
    void getDiagonalCellIds_validAndInvalid() {
        assertNotNull(SheetsHelper.getDiagonalCellIds(3, 4));
        assertNull(SheetsHelper.getDiagonalCellIds(0, 4));
        assertNull(SheetsHelper.getDiagonalCellIds(3, 0));
    }

    @Test
    void createRangeAddress_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> SheetsHelper.createRangeAddress(null));
    }
}
