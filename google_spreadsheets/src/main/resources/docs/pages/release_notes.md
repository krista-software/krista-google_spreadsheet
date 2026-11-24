# Release Notes - Krista Google SpreadSheet Extension
## Version 2.0.7

- **Krista Service APIs Java**: 1.0.126

### What's New
- **MCP Enablement**: Set `tool = true` on all eligible catalog requests so they are exposed as tools to the Krista AI agent (WAIT_FOR_EVENT requests excluded).
- **Dependency Upgrade**: Bumped `krista-apis` and `extension-impl-anno-processors` to `1.0.126`.

---


## Version 2.0.6

- **Developer Name**: Simran Sethi
- **Krista Service APIs Java**: 1.0.121
- **Global Catalog Version**: GC-2026.5.2

### New Feature
**Add Rows to Sheet**: New catalog request that appends rows to the end of existing data in a Google Sheets spreadsheet. Unlike the existing "Append Row Data" request which requires `[ [ Text ] ]` input, this request accepts a flexible `List` input that supports composite/Map row objects (from Krista UI table inputs), nested lists, and simple values. The output returns the number of rows appended instead of cell count.

### Implementation Details

- **New Catalog Request**: Added `appendRowsAtTheEndOfSpreadsheet` in `BasicArea.java` with `@Field.Desc(name = "Rows", type = "List") List<Object>` input parameter.
- **Service Layer**: Added `appendRowsOfSheet` in `BasicAreaService.java` with standard `executeOperation` error handling.
- **Row Conversion**: Added `appendRowsOfSheet` and `convertObjectRows` in `GoogleSheet.java` to handle flexible input types (Map/composite, List, and simple values) and convert them to `List<List<String>>` for the Google Sheets API.
- **Documentation**: Added catalog request documentation page and updated sidebar navigation.

---

## Version 2.0.5

- **Developer Name**: Ramgopal Patidar
- **Krista Service APIs Java**: 1.0.120
- **Global Catalog Version**: GC-2026.2.2

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


