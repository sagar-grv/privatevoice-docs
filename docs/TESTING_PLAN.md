# Testing Plan

## Automated now

- MIME allowlist/rejection
- Streaming SHA-256 copy and empty digest
- Successful import metadata
- Duplicate-copy cleanup
- Unsupported type rejection before copy
- Retryable deletion and startup reconciliation
- ViewModel document flow and per-item batch outcomes
- Room schema compilation, unique hash, and document→page/chunk cascade test source

## Commands

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat compileDebugAndroidTestSources
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat assembleDebug
```

## Manual/device matrix

- Android 12/API 31 ARM64 target and a current API 36 target
- Pick digital PDF, JPEG, PNG, empty/inaccessible item, and a duplicate
- Airplane mode import/list/delete
- Restart during import staging and during deletion
- Confirm no internet permission in merged manifest
- Confirm backup/device-transfer exclusions
- Font scaling, screen reader, high contrast, and large touch targets

Milestone 3 adds synthetic digital/scanned English, Hindi, Marathi, mixed-language, table-heavy, repeated-header, empty-page, and no-answer fixtures.
