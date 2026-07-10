# Security Policy

## Reporting

Do not open public issues containing private documents, recordings, voice profiles, keys, or exploit details. Use a private maintainer security channel once one is established.

## Current boundary

This milestone stores imported copies in Android app-private storage, disables backup/device transfer, and requests no internet permission. Deletion removes database metadata and the document namespace. Physical overwrite on flash storage is not guaranteed.

The Room database is not yet fully encrypted. Do not add voice profiles, raw recordings, or other high-sensitivity artifacts until the Keystore-backed file/database encryption design in `docs/PRIVACY_MODEL.md` is implemented and reviewed.
