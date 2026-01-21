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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static app.krista.extensions.essentials.spreadsheets.google_sheets.constants.CatalogConstants.*;

/**
 * Centralized executor for Google API calls with retry logic and proper error handling.
 * This class handles transient network failures, timeouts, and provides consistent logging.
 *
 * <p>Rate limiting is enforced per-user to respect Google's quota limits (100 requests per 100 seconds per user).
 * Each user can make requests independently without blocking other users.</p>
 */
public final class GoogleApiExecutor {

    private static final Logger logger = LoggerFactory.getLogger(GoogleApiExecutor.class);

    /**
     * Per-user rate limiting to prevent Google API quota exhaustion.
     * Google Sheets API has a limit of 100 requests per 100 seconds per user.
     * This map tracks the last request time for each user independently.
     */
    private static final ConcurrentHashMap<String, AtomicLong> lastRequestTimePerUser = new ConcurrentHashMap<>();

    /**
     * Minimum interval between API requests in milliseconds.
     * This value (1.2s) ensures we stay well under Google's limit of 100 requests per 100 seconds.
     */
    private static final long MIN_REQUEST_INTERVAL_MS = 1200; // 1.2 seconds between requests per user

    /**
     * Default user ID for cases where user identification is not available.
     * Falls back to global rate limiting behavior.
     */
    private static final String DEFAULT_USER_ID = "global-default-user";

    private GoogleApiExecutor() {
        // Private constructor to prevent instantiation
    }

    /**
     * Execute a Google API request with retry logic for transient failures.
     * Uses default user ID for backward compatibility.
     *
     * @param request The API request to execute
     * @param context Context description for logging (e.g., "append cells to sheet")
     * @param <T> The return type of the API call
     * @return The result of the API call
     * @throws IOException If the API call fails after all retry attempts
     * @deprecated Use {@link #executeWithRetry(GoogleApiRequest, String, String)} with explicit userId
     */
    @Deprecated
    public static <T> T executeWithRetry(GoogleApiRequest<T> request, String context) throws IOException {
        return executeWithRetry(request, context, DEFAULT_USER_ID);
    }

    /**
     * Execute a Google API request with retry logic for transient failures.
     * Enforces per-user rate limiting to respect Google's quota limits.
     *
     * @param request The API request to execute
     * @param context Context description for logging (e.g., "append cells to sheet")
     * @param userId The user identifier for rate limiting (typically email#clientId#clientSecret)
     * @param <T> The return type of the API call
     * @return The result of the API call
     * @throws IOException If the API call fails after all retry attempts
     */
    public static <T> T executeWithRetry(GoogleApiRequest<T> request, String context, String userId) throws IOException {
        // Enforce minimum spacing between requests to prevent rate limiting (per user)
        enforceRequestSpacing(userId);

        return executeWithRetry(request, context, userId, DEFAULT_MAX_RETRY_ATTEMPTS);
    }

    /**
     * Execute a Google API request with configurable retry attempts.
     * Enforces per-user rate limiting to respect Google's quota limits.
     *
     * @param request The API request to execute
     * @param context Context description for logging
     * @param userId The user identifier for rate limiting
     * @param maxRetries Maximum number of retry attempts
     * @param <T> The return type of the API call
     * @return The result of the API call
     * @throws IOException If the API call fails after all retry attempts
     */
    public static <T> T executeWithRetry(GoogleApiRequest<T> request, String context, String userId, int maxRetries) throws IOException {
        int attempts = 0;
        IOException lastException = null;

        while (attempts < maxRetries) {
            try {
                T result = request.execute();
                if (attempts > 0) {
                    logger.info("Google API call succeeded after {} retry attempt(s) - Context: {}", attempts, context);
                }
                return result;
            } catch (GoogleJsonResponseException cause) {
                lastException = cause;
                attempts++;

                // Check if error is retriable based on HTTP status code
                if (attempts < maxRetries && isRetriableHttpStatus(cause.getStatusCode())) {
                    long delay = calculateRetryDelay(attempts);
                    logger.warn("Google API error on attempt {}/{} - Context: {} - Status: {} - Retrying in {}ms - Error: {}",
                        attempts, maxRetries, context, cause.getStatusCode(), delay, ErrorMessageFormatter.getErrorSummary(cause));
                    sleep(delay);
                } else {
                    // Non-retriable error (404, 400, 401, 403, etc.)
                    logger.error("Google API error - Context: {} - Status: {} - Error: {}",
                        context, cause.getStatusCode(), ErrorMessageFormatter.getErrorSummary(cause));

                    // Wrap with user-friendly message
                    String userMessage = ErrorMessageFormatter.formatErrorMessage(cause, context);
                    throw new IOException(userMessage, cause);
                }
            } catch (SocketTimeoutException cause) {
                lastException = cause;
                attempts++;
                if (attempts < maxRetries) {
                    long delay = calculateRetryDelay(attempts);
                    logger.warn("Google API connection timeout on attempt {}/{} - Context: {} - Retrying in {}ms - Error: {}",
                        attempts, maxRetries, context, delay, cause.getMessage());
                    sleep(delay);
                } else {
                    logger.error("Google API connection timeout after {} attempts - Context: {} - Error: {}",
                        maxRetries, context, cause.getMessage());
                }
            } catch (IOException cause) {
                lastException = cause;
                attempts++;
                if (attempts < maxRetries && isRetriableIOException(cause)) {
                    long delay = calculateRetryDelay(attempts);
                    logger.warn("Google API network error on attempt {}/{} - Context: {} - Retrying in {}ms - Error: {}",
                        attempts, maxRetries, context, delay, cause.getMessage());
                    sleep(delay);
                } else {
                    logger.error("Google API network error - Context: {} - Error: {}", context, cause.getMessage());

                    // Wrap with user-friendly message
                    String userMessage = ErrorMessageFormatter.formatErrorMessage(cause, context);
                    throw new IOException(userMessage, cause);
                }
            }
        }

        // All retries exhausted - wrap with user-friendly message
        logger.error("Google API call failed after {} attempts - Context: {} - Final error: {}",
            maxRetries, context, ErrorMessageFormatter.getErrorSummary(lastException));

        String userMessage = ErrorMessageFormatter.formatErrorMessage(lastException, context);
        throw new IOException(userMessage, lastException);
    }

    /**
     * Calculate exponential backoff delay with jitter to avoid thundering herd.
     *
     * @param attemptNumber The current attempt number (1-based)
     * @return Delay in milliseconds
     */
    private static long calculateRetryDelay(int attemptNumber) {
        long delay = Math.min(
            DEFAULT_INITIAL_RETRY_DELAY_MS * (long) Math.pow(2, attemptNumber - 1), 
            DEFAULT_MAX_RETRY_DELAY_MS
        );
        // Add jitter (±20%) to prevent synchronized retries
        long jitter = (long) (delay * 0.2 * (Math.random() - 0.5));
        return delay + jitter;
    }

    /**
     * Check if HTTP status code is retriable.
     *
     * @param statusCode The HTTP status code
     * @return true if the status code indicates a retriable error
     */
    private static boolean isRetriableHttpStatus(int statusCode) {
        return statusCode == 429  // Too Many Requests
            || statusCode == 500  // Internal Server Error
            || statusCode == 502  // Bad Gateway
            || statusCode == 503  // Service Unavailable
            || statusCode == 504; // Gateway Timeout
    }

    /**
     * Check if an IOException is retriable based on error message patterns.
     *
     * @param cause The IOException to check
     * @return true if the exception is retriable, false otherwise
     */
    private static boolean isRetriableIOException(IOException cause) {
        String message = cause.getMessage();
        if (message == null) {
            return false;
        }

        String lowerMessage = message.toLowerCase();

        // Check for common retriable error patterns
        return lowerMessage.contains("timeout")
            || lowerMessage.contains("timed out")
            || lowerMessage.contains("connection reset")
            || lowerMessage.contains("connection refused")
            || lowerMessage.contains("temporary failure")
            || lowerMessage.contains("503") // Service Unavailable
            || lowerMessage.contains("429") // Too Many Requests
            || lowerMessage.contains("500") // Internal Server Error
            || lowerMessage.contains("502") // Bad Gateway
            || lowerMessage.contains("504"); // Gateway Timeout
    }

    /**
     * Enforce minimum spacing between API requests to prevent rate limiting.
     * Google Sheets API has a limit of 100 requests per 100 seconds per user.
     * This method ensures we stay well under that limit on a per-user basis.
     *
     * <p>Uses Compare-And-Set (CAS) to handle concurrent requests from the same user
     * in a thread-safe manner without blocking requests from different users.</p>
     *
     * @param userId The user identifier for rate limiting
     */
    private static void enforceRequestSpacing(String userId) {
        // Get or create the AtomicLong for this user
        AtomicLong userLastRequestTime = lastRequestTimePerUser.computeIfAbsent(
            userId,
            k -> new AtomicLong(0)
        );

        while (true) {
            long lastTime = userLastRequestTime.get();
            long currentTime = System.currentTimeMillis();
            long timeSinceLastRequest = currentTime - lastTime;

            if (timeSinceLastRequest >= MIN_REQUEST_INTERVAL_MS) {
                // Enough time has passed, try to claim this time slot
                if (userLastRequestTime.compareAndSet(lastTime, currentTime)) {
                    // Successfully claimed the time slot
                    logger.debug("Rate limiting: User {} proceeding with request ({}ms since last request)",
                        maskUserId(userId), timeSinceLastRequest);
                    break;
                }
                // CAS failed, another thread claimed it, retry the loop
            } else {
                // Not enough time has passed, sleep and retry
                long sleepTime = MIN_REQUEST_INTERVAL_MS - timeSinceLastRequest;
                logger.debug("Rate limiting: User {} sleeping for {}ms to prevent API quota exhaustion",
                    maskUserId(userId), sleepTime);
                sleep(sleepTime);
            }
        }
    }

    /**
     * Mask user ID for logging to avoid exposing sensitive information.
     * Shows only the first part (email) and masks the credentials.
     *
     * @param userId The full user ID (email#clientId#clientSecret)
     * @return Masked user ID for safe logging
     */
    private static String maskUserId(String userId) {
        if (userId == null || userId.equals(DEFAULT_USER_ID)) {
            return DEFAULT_USER_ID;
        }

        int hashIndex = userId.indexOf('#');
        if (hashIndex > 0) {
            return userId.substring(0, hashIndex) + "#***";
        }

        return userId.substring(0, Math.min(10, userId.length())) + "***";
    }

    /**
     * Sleep with interruption handling.
     *
     * @param milliseconds Time to sleep in milliseconds
     */
    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            logger.warn("Retry delay interrupted - Thread: {}", Thread.currentThread().getName());
        }
    }

    /**
     * Functional interface for Google API requests that can throw IOException.
     *
     * @param <T> The return type of the API call
     */
    @FunctionalInterface
    public interface GoogleApiRequest<T> {
        /**
         * Execute the Google API request.
         *
         * @return The result of the API call
         * @throws IOException If the API call fails
         */
        T execute() throws IOException;
    }
}

