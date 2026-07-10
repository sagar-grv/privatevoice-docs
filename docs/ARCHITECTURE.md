# Architecture

## Layers

```text
Compose screens
    ↓ immutable StateFlow
ViewModels
    ↓ domain contracts
Repositories
    ↓
Room DAOs + app-private storage
```

`AppContainer` wires the graph without Hilt. `DocumentLibraryViewModel` exposes display state and batch outcomes. `OfflineDocumentRepository` serializes imports, coordinates duplicate detection, maps persistence records to domain documents, and reconciles interrupted deletion. `AndroidPrivateDocumentStorage` owns SAF metadata reads, staging, hashing, atomic promotion, and namespace deletion.

## Persistence

Room schema version 1 contains the eight foundations requested by the product brief: documents, pages, chunks, conversations, messages, model packs, voice profiles, and voice recordings. Only document behavior is active. Foreign keys cascade documents to pages/chunks, conversations to messages, and voice profiles to recordings.

## Import state

```text
SAF URI → inspect → validate → stage/copy/hash → promote → duplicate check → Room insert
```

Imports are serialized. A Room unique index on `fileHash` is the final race guard. Every selected URI yields success, duplicate, or failure feedback.

## Deletion state

Records are marked `DELETING` before file cleanup. A failed cleanup remains visible and retryable. Startup reconciliation is serialized with imports, clears abandoned staging directories, removes final directories that have no Room record, and retries deleting records. Operations are idempotent.

## Deferred boundaries

PDF extraction, OCR, cleaning, chunking, embeddings, retrieval, LLM, STT, TTS, and personal voice engines will be introduced behind the interfaces described in the original product brief. No online fallback is permitted.
