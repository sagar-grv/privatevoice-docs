# AI Model Integration

No AI model is bundled or executed in Milestones 1–2.

Future adapters must support explicit load/unload, cancellation, progress/errors, checksum verification, memory limits, and offline operation. Candidate runtime families are LiteRT/LiteRT-LM, ONNX Runtime or sherpa-onnx, and llama.cpp through Android JNI. Selection requires licence review and benchmarks on representative ARM64 Android 12+ devices.

Model packs must live in app-private storage, be activated only after checksum verification, and never be committed. The app must show “model unavailable” rather than produce fake output. There will be no online API fallback.
