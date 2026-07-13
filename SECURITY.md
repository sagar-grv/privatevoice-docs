# Security Policy

## Reporting

Use GitHub's private vulnerability reporting for this repository. Do not put private documents, recordings, API keys, or exploit details in a public issue.

## Web boundary

The PWA is static and has no PrivateVoice Docs server. Documents, extracted text, chunks, and conversations live in IndexedDB. API keys live only in JavaScript memory and are excluded from IndexedDB, exports, URLs, logs, and service-worker caches. Hosted-provider requests go directly from the browser to that provider and include the question plus locally retrieved excerpts.

Imported document text and model output are untrusted. UI rendering uses escaped React text rather than raw HTML; grounded prompts explicitly separate source data from instructions. The service worker precaches static build assets only and defines no runtime cache for provider traffic.

The HTML policy restricts scripts, frames, objects, forms, workers, and network destinations. GitHub Pages cannot set arbitrary response headers, so the CSP is delivered as a meta policy; `frame-ancestors` and some header-only protections are therefore unavailable.

## Android boundary

The Android app stores imported copies in app-private storage, disables backup/device transfer, and requests no Internet permission. Deletion removes database metadata and the document namespace; physical overwrite on flash storage is not guaranteed. The Room database is not yet fully encrypted, so high-sensitivity voice artifacts must wait for the Keystore-backed design in `docs/PRIVACY_MODEL.md`.
