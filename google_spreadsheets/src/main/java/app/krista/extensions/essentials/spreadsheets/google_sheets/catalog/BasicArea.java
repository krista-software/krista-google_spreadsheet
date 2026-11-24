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
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.spreadsheets.google_sheets.service.BasicAreaService;
import app.krista.extensions.essentials.spreadsheets.google_sheets.util.ValidationHelper;
import app.krista.model.base.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Domain(id = "catEntryDomain_91825fb8-54b7-445e-90ea-a0f92b347a24",
        name = "Spreadsheets",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "e1cc9887-8d39-4291-9ceb-7fb9e45b88da")
public final class BasicArea {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicArea.class);
    private final BasicAreaService basicAreaService;

    @Inject
    public BasicArea(BasicAreaService basicAreaService) {

        this.basicAreaService = basicAreaService;
    }

    @CatalogRequest(
            id = "localDomainRequest_9d7d96d2-0781-460b-8428-c4681404f754",
            name = "Add data rows From Csv",
            description = "Add data rows From Csv",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Text(name = "Add CSV data result", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse addDataRowsFromCsv(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field.File(name = "CSV file", multipleFileUpload = false, required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) File cSVFile) throws IOException {
        return basicAreaService.addDataRowsFromCsv(spreadsheetId, sheetName, cSVFile);
    }

    @CatalogRequest(
            id = "localDomainRequest_83d11bf1-1d7d-43fe-b84f-d4b00d98ddc8",
            name = "Clear spreadsheet cells",
            description = "Clear spreadsheet cells",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Text(name = "Clear spreadsheet cells result", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse clearSpreadsheetCells(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field.Text(name = "Top left cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String topLeftCell,
            @Field.Text(name = "Bottom right cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String bottomRightCell) {
        ValidationHelper.validateCellIds(topLeftCell, bottomRightCell);
        return basicAreaService.clearSpreadsheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
    }

    @CatalogRequest(
            id = "localDomainRequest_5968e2af-4827-42e2-8849-8980508332e1",
            name = "Delete sheet columns",
            description = "Delete sheet columns",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Text(name = "Delete sheet columns result", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse deleteSheetColumns(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field(name = "Column start index", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double columnStartIndex,
            @Field(name = "Column end index", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double columnEndIndex) {
        return basicAreaService.deleteSheetColumns(spreadsheetId, sheetName, columnStartIndex, columnEndIndex);
    }

    @CatalogRequest(
            id = "localDomainRequest_77f03a68-1e11-40bd-aafa-5040e4bef730",
            name = "Delete sheet rows",
            description = "Delete sheet rows",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Text(name = "Delete sheet rows result", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse deleteSheetRows(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field(name = "Row start index", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double rowStartIndex,
            @Field(name = "Row end index", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double rowEndIndex) {
        return basicAreaService.deleteSheetRows(spreadsheetId, sheetName, rowStartIndex, rowEndIndex);
    }


    @CatalogRequest(
            id = "localDomainRequest_bfbc797a-6d9c-4d0e-b5a2-2a6096ca76b9",
            name = "Add empty sheet columns",
            description = "Add empty sheet columns",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Text(name = "Add empty sheet columns result", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse addEmptySheetColumns(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field(name = "Column start index", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double columnStartIndex,
            @Field(name = "Column end index", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double columnEndIndex) {
        return basicAreaService.addEmptySheetColumns(spreadsheetId, sheetName, columnStartIndex, columnEndIndex);
    }

    @CatalogRequest(
            id = "localDomainRequest_be2e3758-ec2e-441a-918e-49d5c41cb54f",
            name = "Add empty sheet rows",
            description = "Add empty sheet rows",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Text(name = "Add empty sheet rows result", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse addEmptySheetRows(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field(name = "Row start index", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double rowStartIndex,
            @Field(name = "Row end index", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double rowEndIndex) {
        return basicAreaService.addEmptySheetRows(spreadsheetId, sheetName, rowStartIndex, rowEndIndex);
    }

    @CatalogRequest(
            id = "localDomainRequest_e0c9e6e5-79f1-484d-9f49-01b3fc05fe11",
            name = "Append Row Data at the End of Spreadsheet",
            description = "This method allows you to add data to the next available row after the existing data.",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field(name = "Append cells result", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse appendRowDataAtTheEndOfSpreadsheet(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field.Desc(name = "Cell data", type = "[ [ Text ] ]", required = true) List<List<String>> cellData) {
        LOGGER.info("'Append cells of sheet' initialised");
        return basicAreaService.appendCellsOfSheet(spreadsheetId, sheetName, cellData);
    }

    @CatalogRequest(
            id = "localDomainRequest_3f2a4e1b-8c7d-4b9e-a5f6-2d3c7e8b9a0f",
            name = "Add Rows to Sheet",
            description = "This method allows you to add rows to the next available row after the existing data.",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field(name = "Append rows result", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse appendRowsAtTheEndOfSpreadsheet(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field.Desc(name = "Rows", type = "List") List<Object> rows) {
        LOGGER.info("'Append rows at the end of spreadsheet' initialised");
        return basicAreaService.appendRowsOfSheet(spreadsheetId, sheetName, rows);
    }

    @CatalogRequest(
            id = "localDomainRequest_21166842-ada9-4fb1-8fde-95600920d89c",
            name = "Add data to sheet cells",
            description = "Add data to sheet cells",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field(name = "Add data to sheet cell result", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse addDataToSheetCells(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field.Text(name = "Top left cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String topLeftCell,
            @Field.Text(name = "Bottom right cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String bottomRightCell,
            @Field.Desc(name = "Cell data", type = "[ [ Text ] ]", required = true) List<List<String>> cellData) {
        ValidationHelper.validateCellIds(topLeftCell, bottomRightCell);
        return basicAreaService.addDataToSheetCells(spreadsheetId, sheetName, topLeftCell, bottomRightCell, cellData);
    }

    @CatalogRequest(
            id = "localDomainRequest_55d3cdef-8e9c-44a0-98bd-eba78771613b",
            name = "Create spreadsheet",
            description = "Create a spreadsheet at a given path.\nIf a path is not specified then create it spreadsheet file at the root location of google drive.",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Desc(name = "Drive file", type = "Entity(Drive File)", required = false)
    public ExtensionResponse createSpreadsheet(
            @Field.Text(name = "Spreadsheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetName) throws IOException {
        return basicAreaService.createSpreadsheet(spreadsheetName);
    }

    @CatalogRequest(
            id = "localDomainRequest_4f4ade23-d32c-4dab-ac10-10eb2ddc3c75",
            name = "Find values in sheet",
            description = "Find values in sheet",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Desc(name = "Find values in sheet result", type = "[ Text ]", required = false)
    public ExtensionResponse findValuesInSheet(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field.Text(name = "Top left cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String topLeftCell,
            @Field.Text(name = "Bottom right cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String bottomRightCell,
            @Field.Text(name = "Search value", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String searchValue) {
        ValidationHelper.validateCellIds(topLeftCell, bottomRightCell);
        return basicAreaService.findValuesInSheet(spreadsheetId, sheetName, topLeftCell, bottomRightCell, searchValue);
    }

    @CatalogRequest(
            id = "localDomainRequest_fb1ef461-6e67-48b2-910b-359759f2640f",
            name = "Get sheet rows by range",
            description = "Get sheet rows by range",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field.Desc(name = "Sheet rows by range result", type = "[ [ Text ] ]", required = false)
    public ExtensionResponse getSheetRowsByRange(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field.Text(name = "Top left cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String topLeftCell,
            @Field.Text(name = "Bottom right cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String bottomRightCell) {
        ValidationHelper.validateCellIds(topLeftCell, bottomRightCell);
        return basicAreaService.getSheetRowsByRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
    }

    @CatalogRequest(
            id = "localDomainRequest_5e6e2fdb-20a9-4638-b0ab-884dfe56476b",
            name = "Get sum by sheet range",
            description = "Get sum by sheet range",
            area = "Basic",
            type = CatalogRequest.Type.CHANGE_SYSTEM, tool = true)
    @Field(name = "Get sum by range result", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse getSumBySheetRange(
            @Field.Text(name = "Spreadsheet Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String spreadsheetId,
            @Field.Text(name = "Sheet name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String sheetName,
            @Field.Text(name = "Top left cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String topLeftCell,
            @Field.Text(name = "Bottom right cell", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String bottomRightCell) {
        ValidationHelper.validateCellIds(topLeftCell, bottomRightCell);
        return basicAreaService.getSumBySheetRange(spreadsheetId, sheetName, topLeftCell, bottomRightCell);
    }
}