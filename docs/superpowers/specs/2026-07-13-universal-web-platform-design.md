# PrivateVoice Docs Universal Web Platform Design

## Product decision

PrivateVoice Docs will be a public monorepo containing the existing privacy-first Android client and a new installable web client. The web client is a static PWA: documents, extracted text, chunks, conversations, and provider settings remain in the browser. There is no PrivateVoice Docs server and no bundled third-party API key.

## Scope

- Marketing landing page at `/`.
- Functional document assistant at `#/app`, which remains refresh-safe on static GitHub Pages.
- Local PDF, text, and Markdown import and extraction.
- Browser-local persistence and lexical retrieval with grounded source snippets.
- Direct model connections: local Ollama, OpenRouter (including free model IDs), and configurable OpenAI-compatible endpoints.
- Memory-only secrets by default, explicit network-route disclosure, data export, complete local-data deletion, offline app shell, and installable PWA behavior.
- Existing Android application remains privacy-isolated and continues to omit the Internet permission.

## Architecture

```text
File picker -> browser extraction -> chunks + index -> IndexedDB
                                              |
Question -> lexical retrieval -> grounded prompt -> chosen provider
                                              |
                                     answer + citations
```

The app uses deterministic local retrieval so document search does not require a cloud embedding service. Provider requests receive only the selected snippets and question. Ollama is the fully local route; OpenRouter and custom endpoints are opt-in direct network routes.

## Privacy boundaries

- Original files and indexes never transit a project-owned backend.
- API keys live only in React memory and are never placed in IndexedDB, exports, URLs, logs, or service-worker caches.
- Provider-bound routes display a warning before configuration and identify which content will leave the device.
- Import is explicit; deletion removes document records, chunks, and conversations from IndexedDB.
- Export creates a plaintext data export without API keys; it is not presented as a restorable backup.

## Visual system

The accepted concepts are `docs/design/landing-concept.png` and `docs/design/web-app-concept.png`. The system uses true white, dark ink, forest green, pale sage, and amber only for boundary warnings. Editorial serif display type pairs with compact sans UI type. Layouts favor open rails and thin rules over card grids, with 8-10px radii and minimal shadow.

## Failure handling

- Unsupported, encrypted, empty, oversized (25 MB), or overlong (400-page) files fail per item without discarding successful imports.
- Provider errors remain visible and preserve the draft question.
- Ollama failures explain the local service, origin, CORS, and Private Network Access boundary; localhost is the reliable fallback if a hosted origin cannot reach it.
- An empty local retrieval result blocks remote submission instead of sending an ungrounded prompt.
- Storage quota and IndexedDB errors produce actionable local messages.

## Verification

Unit tests cover chunking, retrieval, provider request construction, route behavior, and import policy. Browser QA covers landing-to-app navigation, local demo import, provider setup, grounded chat, sources, privacy settings, export, deletion, responsive layout, and the deployed hash route. Model and document text are always rendered as escaped React text.
