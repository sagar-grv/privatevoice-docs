# PrivateVoice Docs

PrivateVoice Docs is a local-first document intelligence platform for Android, browsers, and laptops. Import private files, retrieve relevant passages on-device, and ask a model you control with visible source excerpts.

## Products

| Client | Status | Privacy boundary |
|---|---|---|
| [Web PWA](web/) | Functional PDF/TXT/Markdown import, local index, grounded chat, Ollama/OpenRouter/custom providers | Static app with no project backend; data stays in IndexedDB |
| [Android](app/) | Native storage, onboarding, Room schema, document import/library, deletion | No `INTERNET` permission; files stay in app-private storage |

The web app is published at **https://sagar-grv.github.io/privatevoice-docs/** after the Pages workflow completes.

## Web app quick start

```powershell
cd web
npm install
npm run dev
```

Open the displayed local URL. Use **Load a local demo** to test retrieval without adding a private file. Production checks:

```powershell
npm test
npm run lint
npm run build
```

### Model routes

- **Ollama:** free and local. Start Ollama, select a local model, and allow the app origin if your browser requires it. For the hosted site, configure `OLLAMA_ORIGINS=https://sagar-grv.github.io` before starting Ollama. Browser Private Network Access behavior varies; running the PWA from localhost is the reliable fallback.
- **OpenRouter:** enter your own key. The default `openrouter/free` router selects an available free model; availability and limits belong to OpenRouter.
- **OpenAI-compatible:** enter an endpoint, model, and key. It works only when that endpoint permits browser CORS requests. Many providers intentionally do not.

Keys are memory-only and disappear on reload. Questions and retrieved excerpts are sent directly to a selected hosted provider; they never pass through a PrivateVoice Docs server.

## Android build and emulator test

Requirements: JDK 17, Android SDK Platform 36, and Build Tools 35+.

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

In VS Code, start an API 31+ emulator from Android Studio Device Manager or `emulator`, then run:

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
.\gradlew.bat connectedDebugAndroidTest
```

## Web architecture

```text
file picker -> local extraction -> local chunks/lexical retrieval -> IndexedDB
question -> selected excerpts -> Ollama or direct provider -> answer + sources
```

The service worker caches only the static app shell. It does not runtime-cache document content or provider requests. Exported JSON is plaintext and contains document text and conversations; API keys are excluded.

## Known boundaries

- Browser import accepts PDF, TXT, and Markdown, up to 25 MB per file and 400 PDF pages. Scanned PDFs need OCR, which is not yet in the web client.
- The Android client currently implements the private document foundation; on-device extraction, RAG, and speech remain later milestones.
- Hosted-model output can be wrong. Source cards expose the exact local excerpts supplied to the model; they do not automatically prove every generated claim.
- GitHub Pages is static hosting, so provider/browser CORS and localhost access cannot be fixed by a project backend.

See [the privacy model](docs/PRIVACY_MODEL.md), [architecture](docs/ARCHITECTURE.md), [testing plan](docs/TESTING_PLAN.md), and [security policy](SECURITY.md).

## License

Apache-2.0. Contributions are welcome through [CONTRIBUTING.md](CONTRIBUTING.md).
