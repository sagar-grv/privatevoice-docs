package com.privatevoicedocs.data.storage

data class SourceMetadata(
    val displayName: String,
    val mimeType: String?,
    val sizeBytes: Long?,
)

data class StoredDocument(
    val localFilePath: String,
    val sha256: String,
    val sizeBytes: Long,
)

interface PrivateDocumentStorage {
    suspend fun inspect(sourceUri: String): SourceMetadata
    suspend fun copy(sourceUri: String, documentId: String, mimeType: String): StoredDocument
    suspend fun delete(documentId: String): Boolean
    suspend fun cleanupStaging()
}
