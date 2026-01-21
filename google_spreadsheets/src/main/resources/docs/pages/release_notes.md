# Release Notes - Krista Google SpreadSheet Extension
## Version 2.0.5

- **Developer Name**: Ramgopal Patidar
- **Krista Service APIs Java**: 1.0.120
- **Global Catalog Version**: GC-2026.1.2

### BugFix
**KE-2841**:Google Spreadsheet Extension: API timeout occurs intermittently even after retries mechanism.


## Version 2.0.4

- **Developer Name**: Ramgopal Patidar
- **Krista Service APIs Java**: 1.0.115
- **Global Catalog Version**: GC-2025.10.3
- **Release Date**: October 2025

### BugFix
**KE-2607**: Google Spreadsheet: Retry logic

### Implementation Details

- **Centralized Retry Mechanism**: Created `GoogleApiExecutor.java` with automatic retry logic (3 attempts, exponential backoff 1s/2s/4s, ±20% jitter) for transient failures (HTTP 429, 500, 502, 503, 504, timeouts). Integrated into `Spreadsheet.java` and `SheetsInformation.java` for all Google API calls.

- **User-Friendly Error Messages**: Created `ErrorMessageFormatter.java` to convert technical Google API errors into clear, actionable messages. Handles all HTTP status codes (400, 401, 403, 404, 429, 500, 502, 503, 504) and network errors with context-aware guidance. Integrated into `BasicAreaService.java` for all catalog requests.

- **Enhanced Logging & Rate Limiting**: Implemented structured logging with operation context, retry tracking, and separate technical/user messages. Added rate limiting (1.2s minimum spacing) to prevent Google API quota exhaustion (100 requests per 100 seconds).

- **Comprehensive Testing & Documentation**: 102 unit tests passing (100% success rate) including `GoogleApiExecutorTest.java` (15 tests), `ErrorMessageFormatterTest.java` (12 tests), and `GoogleSheetTest.java` (28 tests). Created 17 documentation files including `ERROR_HANDLING.md` and `RETRY_MECHANISM.md`.

---

## Version 2.0.3

- **Developer Name**: Chetan Sonawane, Ramgopal Patidar
- **Krista Service APIs Java**: 1.0.115
- **Global Catalog Version**: GC-2025.10.1
- **Release Date**: October 2025

### BugFix
**KE-2549**: Acuity Google SpreadSheet extension - Exception occurred: node1-accesspoint3:9089 failed to respond Error

### Implementation Details

- **Java 21 upgrade**: Upgraded to Java 21 version and added basic logging and exception handling.


