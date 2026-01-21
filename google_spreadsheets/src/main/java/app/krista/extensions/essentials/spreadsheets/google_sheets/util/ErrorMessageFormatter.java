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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Utility class for formatting user-friendly error messages from Google API exceptions.
 * Converts technical error messages into clear, actionable messages for end users.
 */
public final class ErrorMessageFormatter {

    private ErrorMessageFormatter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Format an exception into a user-friendly error message.
     *
     * @param exception The exception to format
     * @param context The operation context (e.g., "append cells to sheet")
     * @return A user-friendly error message
     */
    public static String formatErrorMessage(Exception exception, String context) {
        if (exception instanceof GoogleJsonResponseException) {
            return formatGoogleApiError((GoogleJsonResponseException) exception, context);
        } else if (exception instanceof SocketTimeoutException) {
            return formatTimeoutError(context);
        } else if (exception instanceof IOException) {
            return formatIOError((IOException) exception, context);
        } else if (exception instanceof IllegalArgumentException) {
            return formatValidationError(exception, context);
        } else {
            return formatGenericError(exception, context);
        }
    }

    /**
     * Format Google API specific errors with HTTP status codes.
     */
    private static String formatGoogleApiError(GoogleJsonResponseException exception, String context) {
        int statusCode = exception.getStatusCode();
        String statusMessage = exception.getStatusMessage();
        String errorMessage = extractErrorMessage(exception);

        switch (statusCode) {
            case 400:
                return String.format("Request validation failed due to invalid parameters: %s. Please verify all required fields are provided with correct values and formats, then try again.", errorMessage);

            case 401:
                return "Authentication has expired and requires renewal. Please click 'Authorize' to reconnect your Google account and restore access to your spreadsheets.";

            case 403:
                return String.format("Access denied due to insufficient permissions: %s. Please ensure you have edit permissions for this spreadsheet or contact the spreadsheet owner to grant access.", errorMessage);

            case 404:
                return "The requested spreadsheet or sheet could not be found. Please verify the spreadsheet ID is correct and the sheet name exists. If the spreadsheet was recently created, please wait a moment and try again.";

            case 429:
                return "Request limit exceeded due to too many operations. Google Sheets has temporarily restricted access. Please wait 1-2 minutes before trying again.";

            case 500:
                return "Google Sheets encountered an internal error while processing your request. This is usually temporary - please wait a few moments and try again.";

            case 502:
            case 503:
            case 504:
                return "Google Sheets service is temporarily unavailable due to maintenance or high load. Please wait a few minutes and try again. If the issue persists, check Google's service status.";
            
            default:
                return String.format("Failed to %s: %s (Error code: %d)", context, errorMessage, statusCode);
        }
    }

    /**
     * Extract the most relevant error message from GoogleJsonResponseException.
     */
    private static String extractErrorMessage(GoogleJsonResponseException exception) {
        try {
            if (exception.getDetails() != null && exception.getDetails().getMessage() != null) {
                return exception.getDetails().getMessage();
            }
        } catch (Exception e) {
            // Fall through to default message
        }
        
        if (exception.getStatusMessage() != null && !exception.getStatusMessage().isEmpty()) {
            return exception.getStatusMessage();
        }
        
        return exception.getMessage();
    }

    /**
     * Format timeout errors with enhanced guidance.
     */
    private static String formatTimeoutError(String context) {
        return String.format("Connection timeout occurred while trying to %s. This may be due to network issues or Google Sheets being temporarily slow. Please check your internet connection and try again. If the problem persists, wait a few minutes before retrying.", context);
    }

    /**
     * Format IO errors with enhanced troubleshooting guidance.
     */
    private static String formatIOError(IOException exception, String context) {
        String message = exception.getMessage();

        if (message == null) {
            return String.format("Network connectivity issue occurred while trying to %s. Please verify your internet connection is stable and try again.", context);
        }

        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("timeout") || lowerMessage.contains("timed out")) {
            return formatTimeoutError(context);
        } else if (lowerMessage.contains("connection reset") || lowerMessage.contains("connection refused")) {
            return String.format("Connection was interrupted while trying to %s. This may be due to network instability or firewall restrictions. Please check your internet connection and network settings.", context);
        } else if (lowerMessage.contains("no route to host") || lowerMessage.contains("unreachable")) {
            return "Unable to reach Google Sheets service due to network connectivity issues. Please verify your internet connection and ensure Google services are not blocked by your firewall.";
        } else if (lowerMessage.contains("ssl") || lowerMessage.contains("certificate")) {
            return "Secure connection failed due to certificate or SSL issues. Please check your system date/time settings and network security configuration, or try again later.";
        } else {
            return String.format("Network error occurred while trying to %s: %s. Please verify your internet connection is stable and try again. If the problem persists, wait a few minutes before retrying.", context, message);
        }
    }

    /**
     * Format validation errors with enhanced user guidance.
     */
    private static String formatValidationError(Exception exception, String context) {
        String message = exception.getMessage();

        if (message == null || message.isEmpty()) {
            return String.format("Input validation failed for %s. Please verify all required fields are provided with valid values and try again.", context);
        }

        // Remove technical prefixes if present and provide better guidance
        message = message.replace("Invalid spreadsheet id or invalid sheet name.", "")
                        .replace("Invalid input parameters", "")
                        .trim();

        if (message.isEmpty()) {
            return "Input validation failed. Please verify your spreadsheet ID is correct and the sheet name exists in the spreadsheet. Ensure all required fields are filled with valid data.";
        }

        // Enhance the message with actionable guidance
        return String.format("Validation error: %s. Please review your input data and ensure all fields contain valid values in the correct format.", message);
    }

    /**
     * Format generic errors.
     */
    private static String formatGenericError(Exception exception, String context) {
        String message = exception.getMessage();
        
        if (message != null && !message.isEmpty()) {
            return String.format("Failed to %s: %s", context, message);
        }
        
        return String.format("An unexpected error occurred while trying to %s. Please try again.", context);
    }

    /**
     * Get a short error summary suitable for logging.
     */
    public static String getErrorSummary(Exception exception) {
        if (exception instanceof GoogleJsonResponseException) {
            GoogleJsonResponseException gje = (GoogleJsonResponseException) exception;
            return String.format("HTTP %d: %s", gje.getStatusCode(), gje.getStatusMessage());
        } else if (exception instanceof SocketTimeoutException) {
            return "Connection timeout";
        } else if (exception instanceof IOException) {
            return "Network error: " + exception.getMessage();
        } else {
            return exception.getClass().getSimpleName() + ": " + exception.getMessage();
        }
    }
}

