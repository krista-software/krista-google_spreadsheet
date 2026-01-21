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

package app.krista.extensions.essentials.spreadsheets.google_sheets.constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SheetConstants {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String CODE = "code";
    public static final String JAXRS_ID = "gsextn";
    public static String MEDIA_URL = "http://platform:9991";
    public static String REFRESH_TOKEN = "google_sheets_refresh_token";

    private SheetConstants() {
    }
}
