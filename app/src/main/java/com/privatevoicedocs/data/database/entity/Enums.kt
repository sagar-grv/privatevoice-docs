package com.privatevoicedocs.data.database.entity

enum class ProcessingStatus {
    IMPORTED, QUEUED, EXTRACTING, OCR_PROCESSING, CHUNKING, EMBEDDING, READY, FAILED, DELETING,
}

enum class ExtractionMethod { DIGITAL_TEXT, OCR, MANUAL_CORRECTION, EMPTY }
enum class MessageRole { USER, ASSISTANT, SYSTEM }
enum class GenerationStatus { PENDING, STREAMING, COMPLETE, FAILED, CANCELLED }
enum class ModelType { EMBEDDING, LLM, STT, TTS, PERSONAL_VOICE }
enum class InstallationStatus { NOT_INSTALLED, INSTALLING, INSTALLED, FAILED }
enum class VoiceProfileStatus { DRAFT, READY, DISABLED, FAILED, DELETING }
