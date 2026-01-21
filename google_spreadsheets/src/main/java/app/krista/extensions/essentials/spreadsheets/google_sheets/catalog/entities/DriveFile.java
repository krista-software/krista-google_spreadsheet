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
        ecosystemVersion ="80fd01ab-7e7a-4c92-9e01-e6c0bd151647")
@Entity(name = "Drive File", id = "localDomainEntity_bb7834a2-1fc0-4ec3-9f20-ef0135b49ea1", primaryKey = "File Id", supportStore = false, options = {})
public class DriveFile {

    @Searchable
    @ToString
    @Field.Text(name = "File name", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String fileName;

    @Searchable
    @ToString
    @Field.Text(name = "File path", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String filePath;

    @Searchable
    @ToString
    @Field.Text(name = "Mime type", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String mimeType;

    @Searchable
    @ToString
    @Field.Text(name = "File Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String fileId;

    @ToString
    @Field.Text(name = "Modified time", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String modifiedTime;

    public DriveFile(String fileName, String fileId) {
        this.fileName = fileName;
        this.fileId = fileId;
    }

    public Map<String, Object> toFields() {
        return Map.of(
                "File name", fileName,
                "File path", filePath,
                "Mime type", mimeType,
                "File Id", fileId,
                "Modified time", modifiedTime

        );
    }

}
