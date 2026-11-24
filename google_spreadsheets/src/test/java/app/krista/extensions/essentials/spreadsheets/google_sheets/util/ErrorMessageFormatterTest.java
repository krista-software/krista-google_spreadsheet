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

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for ErrorMessageFormatter.
 * Tests all HTTP status codes, network errors, and validation scenarios.
 */
@DisplayName("ErrorMessageFormatter Tests")
class ErrorMessageFormatterTest {

    @Nested
    @DisplayName("HTTP Status Code Tests")
    class HttpStatusCodeTests {

        @Test
        @DisplayName("Should format 404 error with enhanced message")
        void shouldFormat404ErrorWithEnhancedMessage() {
            // Given
            GoogleJsonResponseException exception = createGoogleJsonException(404, "Not Found", "Requested entity was not found.");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "append cells to sheet");

            // Then
            assertThat(message).isEqualTo("The requested spreadsheet or sheet could not be found. Please verify the spreadsheet ID is correct and the sheet name exists. If the spreadsheet was recently created, please wait a moment and try again.");
        }

        @Test
        @DisplayName("Should format 401 error with enhanced message")
        void shouldFormat401ErrorWithEnhancedMessage() {
            // Given
            GoogleJsonResponseException exception = createGoogleJsonException(401, "Unauthorized", "Invalid Credentials");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "get spreadsheet data");

            // Then
            assertThat(message).isEqualTo("Authentication has expired and requires renewal. Please click 'Authorize' to reconnect your Google account and restore access to your spreadsheets.");
        }

        @Test
        @DisplayName("Should format 403 error with enhanced message")
        void shouldFormat403ErrorWithEnhancedMessage() {
            // Given
            GoogleJsonResponseException exception = createGoogleJsonException(403, "Forbidden", "The caller does not have permission");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "update cells");

            // Then
            assertThat(message).contains("Access denied due to insufficient permissions");
            assertThat(message).contains("Please ensure you have edit permissions");
            assertThat(message).contains("contact the spreadsheet owner");
        }

        @Test
        @DisplayName("Should format 429 error with enhanced message")
        void shouldFormat429ErrorWithEnhancedMessage() {
            // Given
            GoogleJsonResponseException exception = createGoogleJsonException(429, "Too Many Requests", "Rate limit exceeded");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "create spreadsheet");

            // Then
            assertThat(message).isEqualTo("Request limit exceeded due to too many operations. Google Sheets has temporarily restricted access. Please wait 1-2 minutes before trying again.");
        }

        @Test
        @DisplayName("Should format 500 error with enhanced message")
        void shouldFormat500ErrorWithEnhancedMessage() {
            // Given
            GoogleJsonResponseException exception = createGoogleJsonException(500, "Internal Server Error", "Server error");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "delete rows");

            // Then
            assertThat(message).isEqualTo("Google Sheets encountered an internal error while processing your request. This is usually temporary - please wait a few moments and try again.");
        }

        @ParameterizedTest
        @ValueSource(ints = {502, 503, 504})
        @DisplayName("Should format service unavailable errors with enhanced message")
        void shouldFormatServiceUnavailableErrorsWithEnhancedMessage(int statusCode) {
            // Given
            GoogleJsonResponseException exception = createGoogleJsonException(statusCode, "Service Unavailable", "Service error");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "get data");

            // Then
            assertThat(message).isEqualTo("Google Sheets service is temporarily unavailable due to maintenance or high load. Please wait a few minutes and try again. If the issue persists, check Google's service status.");
        }

        @Test
        @DisplayName("Should format 400 error with enhanced message")
        void shouldFormat400ErrorWithEnhancedMessage() {
            // Given
            GoogleJsonResponseException exception = createGoogleJsonException(400, "Bad Request", "Invalid request parameters");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "add data");

            // Then
            assertThat(message).contains("Request validation failed due to invalid parameters");
            assertThat(message).contains("Please verify all required fields");
        }

        @ParameterizedTest
        @MethodSource("provideUnknownStatusCodes")
        @DisplayName("Should handle unknown status codes gracefully")
        void shouldHandleUnknownStatusCodesGracefully(int statusCode, String expectedMessagePart) {
            // Given
            GoogleJsonResponseException exception = createGoogleJsonException(statusCode, "Unknown", "Unknown error");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "unknown operation");

            // Then
            assertThat(message).contains(expectedMessagePart);
        }

        static Stream<Arguments> provideUnknownStatusCodes() {
            return Stream.of(
                Arguments.of(418, "Failed to unknown operation"), // I'm a teapot
                Arguments.of(451, "Failed to unknown operation"), // Unavailable for legal reasons
                Arguments.of(999, "Failed to unknown operation")  // Non-standard code
            );
        }
    }

    @Nested
    @DisplayName("Network Error Tests")
    class NetworkErrorTests {

        @Test
        @DisplayName("Should format SocketTimeoutException with enhanced message")
        void shouldFormatSocketTimeoutExceptionWithEnhancedMessage() {
            // Given
            SocketTimeoutException exception = new SocketTimeoutException("Read timed out");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "append data to spreadsheet");

            // Then
            assertThat(message).contains("Connection timeout occurred while trying to append data to spreadsheet");
            assertThat(message).contains("This may be due to network issues or Google Sheets being temporarily slow");
            assertThat(message).contains("Please check your internet connection");
        }

        @Test
        @DisplayName("Should format ConnectException with enhanced message")
        void shouldFormatConnectExceptionWithEnhancedMessage() {
            // Given
            ConnectException exception = new ConnectException("Connection refused");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "create spreadsheet");

            // Then
            assertThat(message).contains("Connection was interrupted while trying to create spreadsheet");
            assertThat(message).contains("network instability or firewall restrictions");
            assertThat(message).contains("check your internet connection and network settings");
        }

        @Test
        @DisplayName("Should format UnknownHostException with enhanced message")
        void shouldFormatUnknownHostExceptionWithEnhancedMessage() {
            // Given
            UnknownHostException exception = new UnknownHostException("sheets.googleapis.com");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "get sheet data");

            // Then
            // UnknownHostException extends IOException, so it will be handled by formatIOError
            assertThat(message).contains("Network error occurred while trying to get sheet data");
            assertThat(message).contains("sheets.googleapis.com");
        }

        @Test
        @DisplayName("Should format generic IOException with enhanced message")
        void shouldFormatGenericIOExceptionWithEnhancedMessage() {
            // Given
            IOException exception = new IOException("Generic network error");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "update cells");

            // Then
            assertThat(message).contains("Network error occurred while trying to update cells");
            assertThat(message).contains("Generic network error");
            assertThat(message).contains("verify your internet connection is stable");
        }

        @Test
        @DisplayName("Should handle IOException with null message")
        void shouldHandleIOExceptionWithNullMessage() {
            // Given
            IOException exception = new IOException((String) null);

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "test operation");

            // Then
            assertThat(message).contains("Network connectivity issue occurred while trying to test operation");
            assertThat(message).contains("verify your internet connection is stable");
        }
    }

    @Nested
    @DisplayName("Validation Error Tests")
    class ValidationErrorTests {

        @Test
        @DisplayName("Should format validation error with enhanced message")
        void shouldFormatValidationErrorWithEnhancedMessage() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("Invalid cell range");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "clear cells");

            // Then
            assertThat(message).contains("Validation error: Invalid cell range");
            assertThat(message).contains("review your input data");
            assertThat(message).contains("ensure all fields contain valid values");
        }

        @Test
        @DisplayName("Should handle validation error with empty message")
        void shouldHandleValidationErrorWithEmptyMessage() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "validate input");

            // Then
            assertThat(message).contains("Input validation failed for validate input");
            assertThat(message).contains("verify all required fields are provided");
        }

        @Test
        @DisplayName("Should handle validation error with null message")
        void shouldHandleValidationErrorWithNullMessage() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException((String) null);

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "process data");

            // Then
            assertThat(message).contains("Input validation failed for process data");
            assertThat(message).contains("verify all required fields are provided");
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null exception gracefully")
        void shouldHandleNullExceptionGracefully() {
            // When & Then
            assertThatThrownBy(() -> ErrorMessageFormatter.formatErrorMessage(null, "test context"))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle null context gracefully")
        void shouldHandleNullContextGracefully() {
            // Given
            IOException exception = new IOException("Test error");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, null);

            // Then
            assertThat(message).isNotNull();
            assertThat(message).contains("Test error");
        }

        @Test
        @DisplayName("Should handle empty context gracefully")
        void shouldHandleEmptyContextGracefully() {
            // Given
            IOException exception = new IOException("Test error");

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "");

            // Then
            assertThat(message).isNotNull();
            assertThat(message).contains("Test error");
        }

        @Test
        @DisplayName("Should handle very long error messages")
        void shouldHandleVeryLongErrorMessages() {
            // Given
            String longMessage = "This is a very long error message that exceeds normal length expectations and contains many details about what went wrong in the system during processing of the request that was submitted by the user through the application interface.";
            IOException exception = new IOException(longMessage);

            // When
            String message = ErrorMessageFormatter.formatErrorMessage(exception, "long operation");

            // Then
            assertThat(message).isNotNull();
            assertThat(message).contains(longMessage);
            assertThat(message.length()).isGreaterThan(longMessage.length()); // Should include additional context
        }
    }

    /**
     * Helper method to create GoogleJsonResponseException for testing
     */
    private GoogleJsonResponseException createGoogleJsonException(int statusCode, String reasonPhrase, String message) {
        GoogleJsonError error = new GoogleJsonError();
        error.setCode(statusCode);
        error.setMessage(message);

        HttpHeaders headers = new HttpHeaders();

        return new GoogleJsonResponseException(
            new HttpResponseException.Builder(statusCode, reasonPhrase, headers),
            error
        );
    }
}

