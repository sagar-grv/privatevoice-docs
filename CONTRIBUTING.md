# Contributing

Keep changes milestone-scoped, offline-first, and independently testable.

1. Do not add network APIs, analytics, advertising, user documents, recordings, model files, keys, or secrets.
2. Add a failing test before production behavior.
3. Run `testDebugUnitTest` and `assembleDebug` before review.
4. Run instrumentation tests when a device/emulator is available.
5. Document model/runtime licences and memory/storage impact before adding a dependency.
6. Never present mocks as real AI, speech, or personal-voice output.
