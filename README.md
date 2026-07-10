# PrivateVoice Docs

PrivateVoice Docs is a native Android application for asking questions about private documents without the app uploading them. The intended complete product imports documents, processes them locally, answers with grounded page citations, and can speak answers using offline voices.

## Current implementation status

Milestones 1 and 2 are implemented:

- First-run privacy onboarding
- Material 3 Compose navigation for Documents, Chat, and Privacy
- PDF/JPG/JPEG/PNG selection through Android Storage Access Framework
- Staged copy into app-private storage with SHA-256 hashing
- Content-based duplicate detection
- Room-backed document library and all eight requested schema foundations
- Confirmed document deletion with page/chunk cascade and retryable cleanup
- Backup and device-transfer exclusions
- No `INTERNET` permission, analytics, advertising, account, or cloud fallback

PDF extraction, OCR, chunking, embeddings, retrieval, local LLM inference, speech, and personal voice cloning are pending. The Chat screen labels local RAG as unavailable and never generates mock answers.

## Architecture

```text
Compose UI → StateFlow ViewModels → domain repositories → Room + app-private files
```

Android URI/file APIs are isolated behind `PrivateDocumentStorage`. Model-dependent capabilities will use separate interfaces in later milestones. See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Build

Requirements:

- JDK 17
- Android SDK Platform 36
- Android SDK Build Tools 35.0.0 or newer 36.x

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Test

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat compileDebugAndroidTestSources
.\gradlew.bat connectedDebugAndroidTest
```

The final command requires an API 31+ device or emulator. Test planning is in [docs/TESTING_PLAN.md](docs/TESTING_PLAN.md).

## Offline guarantees

- The manifest does not request `android.permission.INTERNET`.
- Imported sources are copied into `filesDir/documents/<id>`.
- Cloud backup and device transfer exclude app files, databases, and preferences.
- The Android system file picker may display cloud-backed providers; PrivateVoice Docs itself does not upload a selected file.

## Model setup

No model pack is bundled or downloaded in these milestones. Large model formats and model directories are ignored by Git. Future integration requirements are documented in [docs/AI_MODEL_INTEGRATION.md](docs/AI_MODEL_INTEGRATION.md).

## Known limitations

- Imported documents remain in `IMPORTED` status; extraction begins in Milestone 3.
- Image/PDF contents are not yet previewed or searchable.
- A device/emulator is required to execute Room instrumentation tests and verify installation/navigation.
- Logical deletion removes database records and the app-private namespace, but flash storage cannot guarantee physical overwrite.
- The Room database is not yet fully encrypted. Sensitive voice/model artifacts are not created in this milestone; the encryption plan is documented before those features are enabled.

## Privacy and voice warnings

Do not treat this foundation build as a complete secure document assistant until the remaining privacy hardening and AI milestones are implemented and tested.

Create a personal voice only for yourself or for a person who has given explicit permission. Personal voice inference is not implemented in this build.
