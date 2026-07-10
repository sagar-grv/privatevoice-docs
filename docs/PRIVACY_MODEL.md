# Privacy Model

## Current controls

- No account, analytics, advertising, crash upload, or internet permission.
- Private working copies under Android `filesDir`.
- Android backup and device-transfer exclusions for roots, files, databases, preferences, and external app data.
- Explicit document deletion and startup retry of incomplete cleanup.
- No microphone, voice profile, or generated audio behavior in this milestone.

The Android system picker is outside the app trust boundary and may expose cloud-backed providers. The app opens only URIs the user selects and never treats the external URI as permanent storage.

## Encryption roadmap

Android Keystore alone does not encrypt Room. Before sensitive voice data is enabled:

1. Generate a Keystore-protected master key requiring suitable device protections.
2. Encrypt voice profiles, recordings, model-derived sensitive artifacts, and temporary files with authenticated encryption.
3. Evaluate an actively maintained encrypted SQLite/Room strategy and document its licence and migration plan.
4. Remove plaintext temporary files and verify cleanup after crash/restart.
5. Add device-authentication gates for any export.

Logical deletion cannot promise physical overwrite on flash storage. User-facing copy must describe verified namespace removal instead.
