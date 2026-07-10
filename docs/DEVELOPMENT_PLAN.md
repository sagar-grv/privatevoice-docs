# Development Plan

1. **Complete:** Android foundation and private import/deletion.
2. **Next:** Digital PDF extraction with page-linked Room storage, progress, preview, and partial-failure handling.
3. OCR fallback and review for scanned PDF/image pages.
4. Deterministic cleaning and page-bounded chunking.
5. Local embeddings, cosine retrieval, and retrieval debugging.
6. Offline grounded RAG with programmatic citations and not-found behavior.
7. Offline STT and standard offline TTS.
8. Consent-first personal voice foundation.
9. Real personal-voice inference only after mobile feasibility and benchmarks are proven.

Every milestone must compile and pass its own tests. Heavy models remain external to Git and unavailable in the UI until a real runtime is configured.
