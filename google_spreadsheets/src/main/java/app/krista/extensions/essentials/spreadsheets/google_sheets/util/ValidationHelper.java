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

import java.util.List;

public final class ValidationHelper {

    private ValidationHelper() {
    }

    /**
     * Check String is null or empty
     *
     * @param strParam string to validate
     * @return true, if string is null or empty. Otherwise, false
     */
    public static boolean isNullEmptyString(String strParam) {
        return strParam == null || strParam.isBlank();
    }

    /**
     * Check Double is null or negative
     *
     * @param doubleParam Double value to validate
     * @return true, if Double is null or negative. Otherwise, false
     */
    public static boolean isNullNegativeIndex(Double doubleParam) {
        return doubleParam == null || doubleParam < 0;
    }

    /**
     * Check List is null or empty
     *
     * @param listParam List to validate
     * @return true, if List is null or empty. Otherwise, false
     */
    public static boolean isNullEmptyList(List listParam) {
        return listParam == null || listParam.isEmpty();
    }

    public static void validateCellIds(String topLeftCellId, String bottomRightCellId) {
        if (isNullEmptyString(topLeftCellId) || isNullEmptyString(bottomRightCellId)) {
            throw new IllegalArgumentException("Invalid top left cell id or bottom right cell id.");
        }
        if (validateId(topLeftCellId) || validateId(bottomRightCellId)) {
            throw new IllegalArgumentException("Invalid top left cell id or bottom right cell id.");
        }
        if (topLeftCellId.compareTo(bottomRightCellId) >= 0) {
            throw new IllegalArgumentException("Top left cell id cannot be greater than bottom right cell id.");
        }
    }

    private static boolean validateId(String cellId) {
        return !cellId.matches("[a-zA-Z]+[0-9]+");
    }
}
