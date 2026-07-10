package com.privatevoicedocs.domain.model

import com.privatevoicedocs.data.database.entity.ProcessingStatus

data class Document(
    val id: String,
    val displayName: String,
    val mimeType: String,
    val fileSize: Long,
    val pageCount: Int?,
    val status: ProcessingStatus,
    val progress: Int,
    val error: String?,
    val createdAt: Long,
)

sealed interface ImportDocumentResult {
    data class Success(val document: Document) : ImportDocumentResult
    data class Duplicate(val existingDocumentId: String, val displayName: String) : ImportDocumentResult
    data class Failure(val message: String) : ImportDocumentResult
}

sealed interface DeleteDocumentResult {
    data object Success : DeleteDocumentResult
    data object NotFound : DeleteDocumentResult
    data class Failure(val message: String) : DeleteDocumentResult
}
