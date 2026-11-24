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

package app.krista.extensions.essentials.spreadsheets.google_sheets.catalog.entities;


import app.krista.extension.impl.anno.*;

import java.util.Map;


@Domain(id = "catEntryDomain_91825fb8-54b7-445e-90ea-a0f92b347a24",
        name = "Spreadsheets",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion ="044e70dd-b68c-4b5d-9dd2-9ac2b26002a7")
@Entity(name = "Excel sheet", id = "localDomainEntity_4f4f2eb2-5d60-406e-a68d-8c64a9e358dd", primaryKey = "Sheet Id", supportStore = false, options = {})
public class ExcelSheet {

    @Searchable
    @ToString
    @Field.Text(name = "Sheet Name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String sheetName;

    @Searchable
    @ToString
    @Field.Text(name = "Sheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String sheetId;

    @Searchable
    @ToString
    @Field.Text(name = "Sheet Index", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String sheetIndex;

    @Field.Text(name = "Sheet Visibility", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String sheetVisibility;

    @Field.Text(name = "Bottom Right Cell Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String bottomRightCellId;

    @Field.Text(name = "Top Left Cell Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String topLeftCellId;

    @Field.Text(name = "Excel File Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String excelFileId;

    @Field.Text(name = "Excel File name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String excelFileName;


    public ExcelSheet(String excelFileId, String excelFileName, String sheetId,
                      String sheetName, String topLeftCellId, String bottomRightCellId) {
        this.excelFileId = excelFileId;
        this.excelFileName = excelFileName;
        this.sheetId = sheetId;
        this.sheetName = sheetName;
        this.topLeftCellId = topLeftCellId;
        this.bottomRightCellId = bottomRightCellId;
    }


    public ExcelSheet(String excelFileId, String sheetName, String topLeftCellId, String bottomRightCellId) {
        this.excelFileId = excelFileId;
        this.sheetName = sheetName;
        this.topLeftCellId = topLeftCellId;
        this.bottomRightCellId = bottomRightCellId;
    }
    public ExcelSheet(String excelFileId, String sheetName) {
        this.excelFileId = excelFileId;
        this.sheetName = sheetName;
    }
    public Map<String, Object> toFields() {
        return Map.of(
                "Excel File Id", excelFileId,
                "Excel File name", excelFileName,
                "Sheet Id", sheetId,
                "Sheet Name", sheetName,
                "Top Left Cell Id", topLeftCellId,
                "Bottom Right Cell Id", bottomRightCellId
        );
    }

}