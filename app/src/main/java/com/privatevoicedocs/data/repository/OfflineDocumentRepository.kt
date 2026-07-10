package com.privatevoicedocs.data.repository

import com.privatevoicedocs.data.database.entity.DocumentEntity
import com.privatevoicedocs.data.database.entity.ProcessingStatus
import com.privatevoicedocs.data.storage.PrivateDocumentStorage
import com.privatevoicedocs.document.importing.DocumentImportPolicy
import com.privatevoicedocs.domain.model.DeleteDocumentResult
import com.privatevoicedocs.domain.model.Document
import com.privatevoicedocs.domain.model.ImportDocumentResult
import com.privatevoicedocs.domain.repository.DocumentRepository
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OfflineDocumentRepository(
    private val localDataSource: DocumentLocalDataSource,
    private val storage: PrivateDocumentStorage,
    private val idFactory: () -> String = { UUID.randomUUID().toString() },
    private val clock: () -> Long = System::currentTimeMillis,
) : DocumentRepository {
    private val importMutex = Mutex()

    override val documents: Flow<List<Document>> = localDataSource.observeAll().map { entities ->
        entities.map(DocumentEntity::toDomain)
    }

    override suspend fun importDocument(sourceUri: String): ImportDocumentResult = importMutex.withLock {
        val metadata = try {
            storage.inspect(sourceUri)
        } catch (error: Throwable) {
            return@withLock ImportDocumentResult.Failure(error.userMessage("Unable to read the selected file"))
        }
        val mimeType = metadata.mimeType?.lowercase()
        if (!DocumentImportPolicy.isSupported(mimeType)) {
            return@withLock ImportDocumentResult.Failure("Unsupported file type. Choose a PDF, JPG, JPEG, or PNG file.")
        }
        if (metadata.sizeBytes == 0L) {
            return@withLock ImportDocumentResult.Failure("The selected file is empty.")
        }

        val documentId = idFactory()
        val stored = try {
            storage.copy(sourceUri, documentId, mimeType!!)
        } catch (error: Throwable) {
            storage.delete(documentId)
            return@withLock ImportDocumentResult.Failure(error.userMessage("The private file copy failed"))
        }
        if (stored.sizeBytes == 0L) {
            storage.delete(documentId)
            return@withLock ImportDocumentResult.Failure("The selected file is empty.")
        }

        localDataSource.findByHash(stored.sha256)?.let { existing ->
            storage.delete(documentId)
            return@withLock ImportDocumentResult.Duplicate(existing.id, existing.displayName)
        }

        val now = clock()
        val entity = DocumentEntity(
            id = documentId,
            displayName = metadata.displayName,
            originalUri = sourceUri,
            localFilePath = stored.localFilePath,
            fileHash = stored.sha256,
            mimeType = mimeType,
            fileSize = stored.sizeBytes,
            pageCount = null,
            processingStatus = ProcessingStatus.IMPORTED,
            processingProgress = 0,
            processingError = null,
            createdAt = now,
            updatedAt = now,
        )

        try {
            localDataSource.insert(entity)
        } catch (duplicate: DuplicateDocumentHashException) {
            storage.delete(documentId)
            val existing = localDataSource.findByHash(stored.sha256)
            return@withLock if (existing != null) {
                ImportDocumentResult.Duplicate(existing.id, existing.displayName)
            } else {
                ImportDocumentResult.Failure("A duplicate document was detected.")
            }
        } catch (error: Throwable) {
            storage.delete(documentId)
            return@withLock ImportDocumentResult.Failure(error.userMessage("Unable to save document metadata"))
        }

        ImportDocumentResult.Success(entity.toDomain())
    }

    override suspend fun deleteDocument(id: String): DeleteDocumentResult {
        val entity = localDataSource.findById(id) ?: return DeleteDocumentResult.NotFound
        val deleting = entity.copy(
            processingStatus = ProcessingStatus.DELETING,
            processingError = null,
            updatedAt = clock(),
        )
        localDataSource.update(deleting)
        return finishDeletion(deleting)
    }

    override suspend fun reconcile() {
        storage.cleanupStaging()
        localDataSource.findDeleting().forEach { finishDeletion(it) }
    }

    private suspend fun finishDeletion(entity: DocumentEntity): DeleteDocumentResult {
        val filesDeleted = try {
            storage.delete(entity.id)
        } catch (_: Throwable) {
            false
        }
        if (!filesDeleted) {
            localDataSource.update(
                entity.copy(
                    processingStatus = ProcessingStatus.DELETING,
                    processingError = "Private file cleanup failed. Tap delete to retry.",
                    updatedAt = clock(),
                ),
            )
            return DeleteDocumentResult.Failure("Private file cleanup failed. Try again.")
        }
        return try {
            localDataSource.deleteById(entity.id)
            DeleteDocumentResult.Success
        } catch (error: Throwable) {
            DeleteDocumentResult.Failure(error.userMessage("Database cleanup failed; deletion will be retried"))
        }
    }
}

private fun DocumentEntity.toDomain() = Document(
    id = id,
    displayName = displayName,
    mimeType = mimeType,
    fileSize = fileSize,
    pageCount = pageCount,
    status = processingStatus,
    progress = processingProgress,
    error = processingError,
    createdAt = createdAt,
)

private fun Throwable.userMessage(fallback: String): String =
    message?.takeIf(String::isNotBlank)?.let { "$fallback: $it" } ?: fallback
