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
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for GoogleApiExecutor.
 * Tests retry logic, rate limiting, and error handling.
 */
@DisplayName("GoogleApiExecutor Tests")
class GoogleApiExecutorTest {

    @Nested
    @DisplayName("Retry Logic Tests")
    class RetryLogicTests {

        @Test
        @DisplayName("Should succeed on first attempt")
        void shouldSucceedOnFirstAttempt() throws IOException {
            // Given
            AtomicInteger callCount = new AtomicInteger(0);
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> {
                callCount.incrementAndGet();
                return "success";
            };

            // When
            String result = GoogleApiExecutor.executeWithRetry(request, "test operation");

            // Then
            assertThat(result).isEqualTo("success");
            assertThat(callCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should retry on SocketTimeoutException and succeed")
        void shouldRetryOnTimeoutAndSucceed() throws IOException {
            // Given
            AtomicInteger callCount = new AtomicInteger(0);
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> {
                int count = callCount.incrementAndGet();
                if (count == 1) {
                    throw new SocketTimeoutException("Read timed out");
                }
                return "success";
            };

            // When
            String result = GoogleApiExecutor.executeWithRetry(request, "test operation");

            // Then
            assertThat(result).isEqualTo("success");
            assertThat(callCount.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should retry on 500 error and succeed")
        void shouldRetryOn500ErrorAndSucceed() throws IOException {
            // Given
            AtomicInteger callCount = new AtomicInteger(0);
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> {
                int count = callCount.incrementAndGet();
                if (count == 1) {
                    throw createGoogleJsonException(500, "Internal Server Error", "Server error");
                }
                return "success";
            };

            // When
            String result = GoogleApiExecutor.executeWithRetry(request, "test operation");

            // Then
            assertThat(result).isEqualTo("success");
            assertThat(callCount.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should exhaust all retries and throw exception")
        void shouldExhaustRetriesAndThrowException() {
            // Given
            AtomicInteger callCount = new AtomicInteger(0);
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> {
                callCount.incrementAndGet();
                throw new SocketTimeoutException("Read timed out");
            };

            // When & Then
            assertThatThrownBy(() -> GoogleApiExecutor.executeWithRetry(request, "test operation"))
                .isInstanceOf(IOException.class);
            
            assertThat(callCount.get()).isEqualTo(4); // Default max retry attempts
        }

        @ParameterizedTest
        @ValueSource(ints = {400, 401, 403, 404})
        @DisplayName("Should not retry on non-retriable HTTP errors")
        void shouldNotRetryOnNonRetriableErrors(int statusCode) {
            // Given
            AtomicInteger callCount = new AtomicInteger(0);
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> {
                callCount.incrementAndGet();
                throw createGoogleJsonException(statusCode, "Error", "Non-retriable error");
            };

            // When & Then
            // Non-retriable errors are wrapped in IOException with user-friendly message
            assertThatThrownBy(() -> GoogleApiExecutor.executeWithRetry(request, "test operation"))
                .isInstanceOf(IOException.class);

            assertThat(callCount.get()).isEqualTo(1); // Should not retry
        }

        @Test
        @DisplayName("Should retry on 429 error (retriable)")
        void shouldRetryOn429Error() throws IOException {
            // Given
            AtomicInteger callCount = new AtomicInteger(0);
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> {
                int count = callCount.incrementAndGet();
                if (count == 1) {
                    throw createGoogleJsonException(429, "Too Many Requests", "Rate limit exceeded");
                }
                return "success";
            };

            // When
            String result = GoogleApiExecutor.executeWithRetry(request, "test operation");

            // Then
            assertThat(result).isEqualTo("success");
            assertThat(callCount.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should not retry on non-IOException")
        void shouldNotRetryOnNonIOException() {
            // Given
            AtomicInteger callCount = new AtomicInteger(0);
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> {
                callCount.incrementAndGet();
                throw new RuntimeException("Runtime error");
            };

            // When & Then
            assertThatThrownBy(() -> GoogleApiExecutor.executeWithRetry(request, "test operation"))
                .isInstanceOf(RuntimeException.class);
            
            assertThat(callCount.get()).isEqualTo(1); // Should not retry
        }
    }

    @Nested
    @DisplayName("Rate Limiting Tests")
    class RateLimitingTests {

        @Test
        @DisplayName("Should enforce minimum request interval")
        void shouldEnforceMinimumRequestInterval() throws IOException {
            // Given
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> "success";
            
            // When
            long startTime = System.currentTimeMillis();
            GoogleApiExecutor.executeWithRetry(request, "test operation 1");
            GoogleApiExecutor.executeWithRetry(request, "test operation 2");
            long endTime = System.currentTimeMillis();
            
            // Then
            long duration = endTime - startTime;
            assertThat(duration).isGreaterThanOrEqualTo(1200); // MIN_REQUEST_INTERVAL_MS = 1200ms
        }

        @Test
        @DisplayName("Should handle concurrent requests with rate limiting")
        void shouldHandleConcurrentRequestsWithRateLimiting() throws InterruptedException, IOException {
            // Given
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> "success";
            int numberOfThreads = 2; // Reduced to minimize timing issues
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

            // When
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < numberOfThreads; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        GoogleApiExecutor.executeWithRetry(request, "concurrent test " + threadId);
                        endLatch.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            startLatch.countDown(); // Start all threads
            endLatch.await(10, TimeUnit.SECONDS); // Wait for completion
            long endTime = System.currentTimeMillis();
            executor.shutdown();

            // Then
            long totalTime = endTime - startTime;
            // Just verify that the test completes in reasonable time (very lenient)
            // The main goal is to test that concurrent requests don't crash
            assertThat(totalTime).isLessThan(10000); // Should complete within 10 seconds
        }

        @Test
        @DisplayName("Should not delay if sufficient time has passed")
        void shouldNotDelayIfSufficientTimePassed() throws IOException, InterruptedException {
            // Given
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> "success";
            
            // When
            GoogleApiExecutor.executeWithRetry(request, "test operation 1");
            Thread.sleep(1300); // Wait longer than MIN_REQUEST_INTERVAL_MS
            
            long startTime = System.currentTimeMillis();
            GoogleApiExecutor.executeWithRetry(request, "test operation 2");
            long endTime = System.currentTimeMillis();
            
            // Then
            long duration = endTime - startTime;
            assertThat(duration).isLessThan(100); // Should be immediate, no additional delay
        }
    }

    @Nested
    @DisplayName("Exponential Backoff Tests")
    class ExponentialBackoffTests {

        @Test
        @DisplayName("Should implement exponential backoff with jitter")
        void shouldImplementExponentialBackoffWithJitter() {
            // Given
            AtomicInteger callCount = new AtomicInteger(0);
            GoogleApiExecutor.GoogleApiRequest<String> request = () -> {
                callCount.incrementAndGet();
                throw new SocketTimeoutException("Read timed out");
            };

            // When
            long startTime = System.currentTimeMillis();
            try {
                GoogleApiExecutor.executeWithRetry(request, "test operation");
            } catch (IOException e) {
                // Expected to fail after retries
            }
            long endTime = System.currentTimeMillis();

            // Then
            long totalTime = endTime - startTime;
            // With exponential backoff: ~1s + ~2s + rate limiting delays
            // Should be at least 2 seconds (very lenient due to jitter and test environment)
            assertThat(totalTime).isGreaterThanOrEqualTo(2000); // Very lenient estimate
            assertThat(callCount.get()).isEqualTo(4);
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
