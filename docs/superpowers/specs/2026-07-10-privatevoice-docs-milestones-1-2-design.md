# PrivateVoice Docs Milestones 1–2 Design

## Scope

Build only the native Android foundation and private document import path: onboarding, Compose navigation, document library, honest chat/privacy placeholders, Room persistence, Storage Access Framework import, app-private copies, SHA-256 duplicate detection, and deletion of a document plus derived database rows. PDF extraction, OCR, embeddings, RAG, speech, and voice cloning remain pending.

## Technical baseline

- Kotlin, Jetpack Compose, Material 3, Coroutines, StateFlow, ViewModel, Room, and manual dependency injection.
- `minSdk 31`, `compileSdk 36`, and `targetSdk 36`.
- AGP 8.13.2, Gradle 8.13, Kotlin 2.3.21, Compose BOM 2026.06.00, Room 2.8.4.
- No `INTERNET` permission, cloud SDK, analytics, advertising, or remote fallback.

## Architecture

The dependency direction is `UI → ViewModel → DocumentRepository → Room/private storage`. Compose observes immutable `StateFlow` state. Android URI and file APIs remain behind `PrivateDocumentStorage`; the UI never persists or reopens an external URI as the canonical document source.

Room owns metadata. Foreign-key cascades connect pages and chunks to documents, messages to conversations, and voice recordings to profiles. The schema includes all eight entities requested by the brief so future milestones can add behavior without replacing persistence boundaries.

## Import flow

1. Launch `ActivityResultContracts.OpenMultipleDocuments` for PDF/JPEG/PNG.
2. Validate MIME type and read display metadata.
3. Copy into `filesDir/documents/<id>/source.<extension>` while hashing.
4. Reject empty files and compare the hash against the unique Room index.
5. Delete a newly copied duplicate; otherwise insert an `IMPORTED` record.
6. Clean partial files on every failure path.

## Deletion flow

After UI confirmation, mark the record `DELETING`, remove its private directory, then delete its Room row. Page and chunk rows cascade. If file removal fails, restore the prior status and surface the error. Physical overwrite is not guaranteed on flash storage, so deletion is documented as logical removal plus best-effort file cleanup.

## UI and accessibility

Onboarding explains that documents stay on-device. Documents, Chat, and Privacy are top-level destinations. The library represents loading, empty, imported, duplicate, failure, and delete-confirmation states. Chat explicitly says that local RAG is not installed; it never emits fake answers. Controls use Material 3 sizing, text labels, semantics, and scalable typography.

## Testing

Local tests cover MIME policy, hashing, import outcomes, duplicate cleanup, and deletion behavior. Instrumentation tests cover Room insertion, unique hashes, and cascade deletion. Build verification includes unit tests, debug assembly, merged-manifest inspection, and device tests when an emulator/device exists.

## Next milestone

Milestone 3 adds page-level digital PDF extraction, persisted progress, page storage, and a text-preview screen.
