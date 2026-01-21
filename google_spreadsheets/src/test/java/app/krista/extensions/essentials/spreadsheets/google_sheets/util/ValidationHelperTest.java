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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for ValidationHelper utility class.
 * Tests all validation methods with various edge cases and scenarios.
 */
@DisplayName("ValidationHelper Tests")
class ValidationHelperTest {

    @Nested
    @DisplayName("String Validation Tests")
    class StringValidationTests {

        @Test
        @DisplayName("Should return false for valid non-empty string")
        void shouldReturnFalseForValidString() {
            // Given
            String validString = "valid string";

            // When
            boolean result = ValidationHelper.isNullEmptyString(validString);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for null string")
        void shouldReturnTrueForNullString() {
            // When
            boolean result = ValidationHelper.isNullEmptyString(null);

            // Then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   ", "\t", "\n", "\r", " \t \n \r "})
        @DisplayName("Should return true for null, empty, or blank strings")
        void shouldReturnTrueForNullEmptyOrBlankStrings(String input) {
            // When
            boolean result = ValidationHelper.isNullEmptyString(input);

            // Then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "test", "  test  ", "\ttest\n", "123", "!@#$%"})
        @DisplayName("Should return false for strings with content")
        void shouldReturnFalseForStringsWithContent(String input) {
            // When
            boolean result = ValidationHelper.isNullEmptyString(input);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void shouldHandleUnicodeCharacters() {
            // Given
            String unicodeString = "测试 🚀 émojis";

            // When
            boolean result = ValidationHelper.isNullEmptyString(unicodeString);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Double Validation Tests")
    class DoubleValidationTests {

        @Test
        @DisplayName("Should return false for valid positive double")
        void shouldReturnFalseForValidPositiveDouble() {
            // Given
            Double validDouble = 5.5;

            // When
            boolean result = ValidationHelper.isNullNegativeIndex(validDouble);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for zero")
        void shouldReturnFalseForZero() {
            // Given
            Double zero = 0.0;

            // When
            boolean result = ValidationHelper.isNullNegativeIndex(zero);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for null double")
        void shouldReturnTrueForNullDouble() {
            // When
            boolean result = ValidationHelper.isNullNegativeIndex(null);

            // Then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1.0, -0.1, -100.0, -Double.MAX_VALUE})
        @DisplayName("Should return true for negative doubles")
        void shouldReturnTrueForNegativeDoubles(Double negativeValue) {
            // When
            boolean result = ValidationHelper.isNullNegativeIndex(negativeValue);

            // Then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, 0.1, 1.0, 100.0, Double.MAX_VALUE})
        @DisplayName("Should return false for non-negative doubles")
        void shouldReturnFalseForNonNegativeDoubles(Double nonNegativeValue) {
            // When
            boolean result = ValidationHelper.isNullNegativeIndex(nonNegativeValue);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should handle special double values")
        void shouldHandleSpecialDoubleValues() {
            // Test positive infinity
            assertThat(ValidationHelper.isNullNegativeIndex(Double.POSITIVE_INFINITY)).isFalse();
            
            // Test negative infinity
            assertThat(ValidationHelper.isNullNegativeIndex(Double.NEGATIVE_INFINITY)).isTrue();
            
            // Test NaN
            assertThat(ValidationHelper.isNullNegativeIndex(Double.NaN)).isFalse(); // NaN is not < 0
        }
    }

    @Nested
    @DisplayName("List Validation Tests")
    class ListValidationTests {

        @Test
        @DisplayName("Should return false for valid non-empty list")
        void shouldReturnFalseForValidList() {
            // Given
            List<String> validList = Arrays.asList("item1", "item2");

            // When
            boolean result = ValidationHelper.isNullEmptyList(validList);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for null list")
        void shouldReturnTrueForNullList() {
            // When
            boolean result = ValidationHelper.isNullEmptyList(null);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return true for empty list")
        void shouldReturnTrueForEmptyList() {
            // Given
            List<String> emptyList = Collections.emptyList();

            // When
            boolean result = ValidationHelper.isNullEmptyList(emptyList);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should handle list with null elements")
        void shouldHandleListWithNullElements() {
            // Given
            List<String> listWithNulls = Arrays.asList(null, null, null);

            // When
            boolean result = ValidationHelper.isNullEmptyList(listWithNulls);

            // Then
            assertThat(result).isFalse(); // List is not empty, just contains nulls
        }

        @Test
        @DisplayName("Should handle list with mixed content")
        void shouldHandleListWithMixedContent() {
            // Given
            List<Object> mixedList = Arrays.asList("string", 123, null, true);

            // When
            boolean result = ValidationHelper.isNullEmptyList(mixedList);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Cell ID Validation Tests")
    class CellIdValidationTests {

        @Test
        @DisplayName("Should validate correct cell range successfully")
        void shouldValidateCorrectCellRange() {
            // Given
            String topLeft = "A1";
            String bottomRight = "C10";

            // When & Then
            assertThatCode(() -> ValidationHelper.validateCellIds(topLeft, bottomRight))
                .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @MethodSource("provideValidCellRanges")
        @DisplayName("Should accept various valid cell ranges")
        void shouldAcceptValidCellRanges(String topLeft, String bottomRight) {
            // When & Then
            assertThatCode(() -> ValidationHelper.validateCellIds(topLeft, bottomRight))
                .doesNotThrowAnyException();
        }

        static Stream<Arguments> provideValidCellRanges() {
            return Stream.of(
                Arguments.of("A1", "A2"),
                Arguments.of("A1", "B1"),
                Arguments.of("A1", "Z999"),
                Arguments.of("AA1", "ZZ999"),
                Arguments.of("B5", "D10")
                // Note: A1,A1 is not valid because compareTo("A1", "A1") >= 0 is true
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "   "})
        @DisplayName("Should throw exception for null or empty cell IDs")
        void shouldThrowExceptionForNullOrEmptyCellIds(String invalidCellId) {
            // When & Then
            assertThatThrownBy(() -> ValidationHelper.validateCellIds(invalidCellId, "B2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid top left cell id or bottom right cell id.");

            assertThatThrownBy(() -> ValidationHelper.validateCellIds("A1", invalidCellId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid top left cell id or bottom right cell id.");
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCellIds")
        @DisplayName("Should throw exception for invalid cell ID formats")
        void shouldThrowExceptionForInvalidCellIdFormats(String invalidCellId) {
            // When & Then
            assertThatThrownBy(() -> ValidationHelper.validateCellIds(invalidCellId, "B2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid top left cell id or bottom right cell id.");

            assertThatThrownBy(() -> ValidationHelper.validateCellIds("A1", invalidCellId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid top left cell id or bottom right cell id.");
        }

        static Stream<Arguments> provideInvalidCellIds() {
            return Stream.of(
                Arguments.of("1A"),      // Number before letter
                Arguments.of("A"),       // Missing number
                Arguments.of("1"),       // Missing letter
                Arguments.of("AA"),      // Missing number
                Arguments.of("123"),     // Only numbers
                Arguments.of("ABC"),     // Only letters
                Arguments.of("A1B"),     // Letter after number
                Arguments.of("1A2"),     // Mixed format
                Arguments.of("A-1"),     // Invalid character
                Arguments.of("A 1"),     // Space in cell ID
                Arguments.of("@1"),      // Special character
                Arguments.of("A1.5")     // Decimal in row number
                // Note: "a1" and "A0" are actually valid according to the regex [a-zA-Z]+[0-9]+
            );
        }

        @Test
        @DisplayName("Should throw exception when top left is greater than bottom right")
        void shouldThrowExceptionWhenTopLeftGreaterThanBottomRight() {
            // Given
            String topLeft = "C10";
            String bottomRight = "A1";

            // When & Then
            assertThatThrownBy(() -> ValidationHelper.validateCellIds(topLeft, bottomRight))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Top left cell id cannot be greater than bottom right cell id.");
        }

        @ParameterizedTest
        @MethodSource("provideInvalidRanges")
        @DisplayName("Should throw exception for invalid ranges")
        void shouldThrowExceptionForInvalidRanges(String topLeft, String bottomRight) {
            // When & Then
            assertThatThrownBy(() -> ValidationHelper.validateCellIds(topLeft, bottomRight))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Top left cell id cannot be greater than bottom right cell id.");
        }

        static Stream<Arguments> provideInvalidRanges() {
            return Stream.of(
                Arguments.of("B1", "A1"),    // Column B > A (string comparison)
                Arguments.of("A2", "A1"),    // Row 2 > 1 (string comparison)
                Arguments.of("B2", "A1"),    // Both column and row greater
                Arguments.of("Z999", "A1"),  // Large difference
                Arguments.of("A1", "A1")     // Equal cells are invalid (>= 0)
                // Note: "AA1" vs "Z999" - string comparison "AA1".compareTo("Z999") < 0, so this is valid
            );
        }

        @Test
        @DisplayName("Should handle edge cases for cell comparison")
        void shouldHandleEdgeCasesForCellComparison() {
            // Test boundary cases - these work because of string comparison
            assertThatCode(() -> ValidationHelper.validateCellIds("A9", "B1"))
                .doesNotThrowAnyException();

            assertThatCode(() -> ValidationHelper.validateCellIds("A99", "B1"))
                .doesNotThrowAnyException();

            // Note: "A10" > "A9" in string comparison, so this should work
            assertThatCode(() -> ValidationHelper.validateCellIds("A1", "A10"))
                .doesNotThrowAnyException();
        }
    }
}
