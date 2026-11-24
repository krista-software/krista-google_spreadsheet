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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CSVFileHelper {

    private CSVFileHelper() {
    }

    public static List<List<String>> getRowsFromCSVFile(File file) throws IOException {
        List<List<String>> CSVRecordList = new ArrayList<>();
        CSVParser csvParser = getCSVFileParser(file);
        assert csvParser != null;
        for (CSVRecord csvRecord : csvParser) {
            List<String> csvRow = new ArrayList<>();
            int columnSize = csvRecord.size();
            for (int index = 0; index < columnSize; index++) {
                csvRow.add(csvRecord.get(index));
            }
            CSVRecordList.add(csvRow);
        }
        return CSVRecordList;
    }


    public static CSVParser getCSVFileParser(File file) throws IOException {
        if (!file.isFile()) {
            return null;
        }
        Reader reader = Files.newBufferedReader(Path.of(file.getAbsolutePath()));
        return new CSVParser(reader, CSVFormat.DEFAULT);
    }
}
