# PrivateVoice Docs Milestones 1–2 Design

## Scope

Build only the native Android foundation and private document import path: onboarding, Compose navigation, document library, honest chat/privacy placeholders, Room persistence, Storage Access Framework import, app-private copies, SHA-256 duplicate detection, and deletion of a document plus derived database rows. PDF extraction, OCR, embeddings, RAG, speech, and voice cloning remain pending.

## Technical baseline

- Kotlin, Jetpack Compose, Material 3, Coroutines, StateFlow, ViewModel, Room, and manual dependency injection.
- `minSdk 31`, `compileSdk 36`, and `targetSdk 36`.
- AGP 8.13.2, Gradle 8.13, Kotlin 2.3.21, Compose BOM 2026.06.00, Lifecycle 2.10.0, and Room 2.8.4. Lifecycle 2.11 is intentionally not used because it requires preview API 37/AGP 9.1; the product targets stable Android 16/API 36.
- No `INTERNET` permission, Android backup/device transfer, cloud SDK, analytics, advertising, or remote fallback. The app never uploads a selected file; the Android system picker may itself expose providers backed by cloud services.

## Architecture

The dependency direction is `UI → ViewModel → DocumentRepository → Room/private storage`. Compose observes immutable `StateFlow` state. Android URI and file APIs remain behind `PrivateDocumentStorage`; the UI never persists or reopens an external URI as the canonical document source.

Room owns metadata. Foreign-key cascades connect pages and chunks to documents, messages to conversations, and voice recordings to profiles. The brief explicitly requires schema-only foundations for `Document`, `Page`, `Chunk`, `Conversation`, `Message`, `ModelPack`, `VoiceProfile`, and `VoiceRecording` in Milestones 1–2. Only document behavior is activated; later tables have no UI or synthetic AI/voice behavior.

## Import flow

1. Launch `ActivityResultContracts.OpenMultipleDocuments` for PDF/JPEG/PNG.
2. Validate MIME type and read display metadata.
3. Copy into a staging directory while hashing, then atomically promote the completed private copy to `filesDir/documents/<id>/source.<extension>`.
4. Reject empty files and compare the hash against the unique Room index.
5. Delete a newly copied duplicate; otherwise insert an `IMPORTED` record.
6. Clean partial files on every failure path. Imports are serialized; a unique-index race is still mapped to `Duplicate` and its losing copy is removed. Multi-select returns one outcome per selected item.

At startup, reconciliation removes abandoned staging directories and retries `DELETING` records. Import and deletion recovery are idempotent.

## Deletion flow

After UI confirmation, mark the record `DELETING`, remove its private directory, then delete its Room row. Page and chunk rows cascade. A failure leaves a visible retryable `DELETING` record and startup reconciliation retries it. “Complete deletion” means verified namespace removal and database cleanup; physical overwrite is not guaranteed on flash storage.

## UI and accessibility

Onboarding explains that documents stay on-device. Documents, Chat, and Privacy are top-level destinations. The library represents loading, empty, imported, duplicate, failure, and delete-confirmation states. Chat explicitly says that local RAG is not installed; it never emits fake answers. Controls use Material 3 sizing, text labels, semantics, and scalable typography.

## Testing

Local tests cover MIME policy, hashing, null/security/I/O failures, import outcomes, duplicate cleanup, cancellation boundaries, and idempotent deletion/reconciliation. Instrumentation tests cover Room insertion, unique hashes, cascades, and the import/list/duplicate/delete path. Build verification includes unit tests, debug assembly, merged-manifest inspection for network/backup rules, and device tests when a configured emulator/device exists.

## Next milestone

Milestone 3 adds page-level digital PDF extraction, persisted progress, page storage, and a text-preview screen.
