# PrivateVoice Docs Milestones 1–2 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a compiling offline-first Android foundation with private PDF/image import, duplicate detection, Room-backed listing, and verified logical document deletion.

**Architecture:** A single Compose app module uses StateFlow ViewModels over a domain repository. Room stores metadata and Android-private storage owns copied files; AI and voice work remains explicitly unavailable.

**Tech Stack:** Kotlin 2.3.21, Android SDK 36, AGP 8.13.2, Gradle 8.13, Compose/Material 3, Navigation, Room 2.8.4 with KSP, Coroutines, JUnit, AndroidX test.

---

### Task 1: Build foundation

**Files:** `settings.gradle.kts`, root/app build files, version catalog, manifest, resources, Gradle wrapper.

- [ ] Configure SDK 36 and stable library versions.
- [ ] Add the minimum Compose, lifecycle, navigation, Room, coroutine, and test dependencies.
- [ ] Disable backup/device transfer, verify Gradle configuration, and keep the manifest free of `INTERNET`.

### Task 2: Import primitives with TDD

**Files:** `DocumentImportPolicy.kt`, `Sha256Hasher.kt`, and focused unit tests.

- [ ] Write MIME policy tests and observe failure because production types are absent.
- [ ] Implement PDF/JPEG/PNG validation and make tests pass.
- [ ] Write deterministic streaming-hash tests and observe failure.
- [ ] Implement SHA-256 and make tests pass.

### Task 3: Room schema with tests

**Files:** `data/database/entity/*`, `data/database/dao/*`, `AppDatabase.kt`, converters, instrumentation tests.

- [ ] Add tests for document insert/observe, unique hash, and cascade deletion.
- [ ] Implement the eight explicitly requested schema-only entities, enums, converters, indexes, and foreign keys without enabling later AI/voice behavior.
- [ ] Implement focused DAOs and export schema version 1.

### Task 4: Storage and repository with TDD

**Files:** domain models/repository, storage boundary/Android adapter, `OfflineDocumentRepository.kt`, unit tests.

- [ ] Add failing tests for success, batch outcomes, duplicate/race cleanup, null/security/I/O failures, restart reconciliation, and deletion retry.
- [ ] Implement typed import/delete results and metadata.
- [ ] Implement staged private copies with atomic promotion, hash calculation, and abandoned-stage cleanup.
- [ ] Serialize imports, map unique races to duplicates, implement idempotent deletion reconciliation, and rerun all unit tests.

### Task 5: Compose application

**Files:** application/container, activity, navigation, theme, onboarding, documents, chat, and privacy packages.

- [ ] Add ViewModel state tests.
- [ ] Implement immutable StateFlow ViewModels.
- [ ] Implement onboarding and top-level navigation.
- [ ] Implement SAF import, library feedback, duplicate/error states, and delete confirmation.
- [ ] Implement honest Chat and Privacy placeholders with accessibility semantics.

### Task 6: Documentation and verification

**Files:** repository policy files plus all required `docs/*.md` files.

- [ ] Document architecture, privacy boundary, model exclusions, build/test steps, and limitations.
- [ ] Run `testDebugUnitTest` and `assembleDebug`.
- [ ] Run device tests when a target exists; otherwise record that limitation exactly.
- [ ] Inspect the merged manifest for network permission plus backup/device-transfer exclusions and report APK/test outputs.
