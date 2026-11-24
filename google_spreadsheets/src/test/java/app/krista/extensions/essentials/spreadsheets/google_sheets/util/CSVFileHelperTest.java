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

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVFileHelperTest {

    @Test
    void getRowsFromCSVFile_parsesRowsAndColumns() throws Exception {
        File csv = File.createTempFile("sheets", ".csv");
        csv.deleteOnExit();
        Files.writeString(csv.toPath(), "a,b,c\n1,2,3\n");

        List<List<String>> rows = CSVFileHelper.getRowsFromCSVFile(csv);

        assertEquals(2, rows.size());
        assertEquals("a", rows.get(0).get(0));
        assertEquals("3", rows.get(1).get(2));
    }

    @Test
    void getCSVFileParser_returnsNullForNonFile() throws Exception {
        File dir = Files.createTempDirectory("sheets-dir").toFile();
        dir.deleteOnExit();
        assertNull(CSVFileHelper.getCSVFileParser(dir));
    }

    @Test
    void getCSVFileParser_returnsParserForRealFile() throws Exception {
        File csv = File.createTempFile("sheets", ".csv");
        csv.deleteOnExit();
        Files.writeString(csv.toPath(), "x\n");
        assertNotNull(CSVFileHelper.getCSVFileParser(csv));
    }
}
