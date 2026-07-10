package com.privatevoicedocs.data.repository

import com.privatevoicedocs.data.database.entity.DocumentEntity
import com.privatevoicedocs.data.database.entity.ProcessingStatus
import com.privatevoicedocs.data.storage.PrivateDocumentStorage
import com.privatevoicedocs.data.storage.SourceMetadata
import com.privatevoicedocs.data.storage.StoredDocument
import com.privatevoicedocs.domain.model.DeleteDocumentResult
import com.privatevoicedocs.domain.model.ImportDocumentResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineDocumentRepositoryTest {
    @Test
    fun `successful import stores the private copy metadata`() = runTest {
        val store = FakeDocumentLocalDataSource()
        val storage = FakePrivateDocumentStorage()
        val repository = repository(store, storage)

        val result = repository.importDocument("content://policy")

        assertTrue(result is ImportDocumentResult.Success)
        val saved = store.documents.value.single()
        assertEquals("document-1", saved.id)
        assertEquals("Policy.pdf", saved.displayName)
        assertEquals("hash-policy", saved.fileHash)
        assertEquals(ProcessingStatus.IMPORTED, saved.processingStatus)
    }

    @Test
    fun `duplicate import removes its new private copy and returns the existing document`() = runTest {
        val store = FakeDocumentLocalDataSource().apply { insert(existingDocument()) }
        val storage = FakePrivateDocumentStorage()
        val repository = repository(store, storage)

        val result = repository.importDocument("content://policy")

        assertEquals(ImportDocumentResult.Duplicate("existing", "Existing.pdf"), result)
        assertEquals(listOf("document-1"), storage.deletedDocumentIds)
        assertEquals(1, store.documents.value.size)
    }

    @Test
    fun `failed file deletion remains visibly retryable and reconciliation completes it`() = runTest {
        val store = FakeDocumentLocalDataSource().apply { insert(existingDocument()) }
        val storage = FakePrivateDocumentStorage(deleteSucceeds = false)
        val repository = repository(store, storage)

        val first = repository.deleteDocument("existing")

        assertTrue(first is DeleteDocumentResult.Failure)
        assertEquals(ProcessingStatus.DELETING, store.documents.value.single().processingStatus)

        storage.deleteSucceeds = true
        repository.reconcile()

        assertTrue(store.documents.value.isEmpty())
    }

    @Test
    fun `unsupported MIME type fails before a private copy is created`() = runTest {
        val store = FakeDocumentLocalDataSource()
        val storage = FakePrivateDocumentStorage(
            metadata = SourceMetadata("Notes.txt", "text/plain", 12),
        )
        val repository = repository(store, storage)

        val result = repository.importDocument("content://notes")

        assertTrue(result is ImportDocumentResult.Failure)
        assertEquals(0, storage.copyCount)
        assertTrue(store.documents.value.isEmpty())
    }

    @Test
    fun `reconciliation removes final directories that have no database record`() = runTest {
        val store = FakeDocumentLocalDataSource().apply { insert(existingDocument()) }
        val storage = FakePrivateDocumentStorage()
        val repository = repository(store, storage)

        repository.reconcile()

        assertEquals(setOf("existing"), storage.lastKnownDocumentIds)
    }

    @Test
    fun `database failure while marking deletion returns a failure without deleting files`() = runTest {
        val store = FakeDocumentLocalDataSource().apply {
            insert(existingDocument())
            failUpdates = true
        }
        val storage = FakePrivateDocumentStorage()
        val repository = repository(store, storage)

        val result = repository.deleteDocument("existing")

        assertTrue(result is DeleteDocumentResult.Failure)
        assertTrue(storage.deletedDocumentIds.isEmpty())
    }

    @Test
    fun `cancelled copy performs cleanup and rethrows cancellation`() = runTest {
        val store = FakeDocumentLocalDataSource()
        val storage = FakePrivateDocumentStorage().apply { throwCancellation = true }
        val repository = repository(store, storage)

        try {
            repository.importDocument("content://policy")
            org.junit.Assert.fail("Expected cancellation")
        } catch (_: CancellationException) {
            assertEquals(listOf("document-1"), storage.deletedDocumentIds)
        }
    }

    private fun repository(
        store: FakeDocumentLocalDataSource,
        storage: FakePrivateDocumentStorage,
    ) = OfflineDocumentRepository(
        localDataSource = store,
        storage = storage,
        idFactory = { "document-1" },
        clock = { 1_700_000_000_000L },
    )

    private fun existingDocument() = DocumentEntity(
        id = "existing",
        displayName = "Existing.pdf",
        originalUri = "content://existing",
        localFilePath = "/private/existing/source.pdf",
        fileHash = "hash-policy",
        mimeType = "application/pdf",
        fileSize = 100,
        pageCount = null,
        processingStatus = ProcessingStatus.IMPORTED,
        processingProgress = 0,
        processingError = null,
        createdAt = 1,
        updatedAt = 1,
    )
}

private class FakeDocumentLocalDataSource : DocumentLocalDataSource {
    val documents = MutableStateFlow<List<DocumentEntity>>(emptyList())
    var failUpdates = false

    override fun observeAll(): Flow<List<DocumentEntity>> = documents
    override suspend fun findById(id: String): DocumentEntity? = documents.value.find { it.id == id }
    override suspend fun findByHash(hash: String): DocumentEntity? = documents.value.find { it.fileHash == hash }
    override suspend fun findDeleting(): List<DocumentEntity> =
        documents.value.filter { it.processingStatus == ProcessingStatus.DELETING }
    override suspend fun allIds(): List<String> = documents.value.map(DocumentEntity::id)

    override suspend fun insert(document: DocumentEntity) {
        documents.value = documents.value + document
    }

    override suspend fun update(document: DocumentEntity) {
        if (failUpdates) error("database unavailable")
        documents.value = documents.value.map { if (it.id == document.id) document else it }
    }

    override suspend fun deleteById(id: String) {
        documents.value = documents.value.filterNot { it.id == id }
    }
}

private class FakePrivateDocumentStorage(
    private val metadata: SourceMetadata = SourceMetadata("Policy.pdf", "application/pdf", 100),
    var deleteSucceeds: Boolean = true,
) : PrivateDocumentStorage {
    val deletedDocumentIds = mutableListOf<String>()
    var copyCount = 0
    var lastKnownDocumentIds: Set<String>? = null
    var throwCancellation = false

    override suspend fun inspect(sourceUri: String): SourceMetadata = metadata

    override suspend fun copy(sourceUri: String, documentId: String, mimeType: String): StoredDocument {
        copyCount++
        if (throwCancellation) throw CancellationException("cancelled")
        return StoredDocument("/private/$documentId/source.pdf", "hash-policy", 100)
    }

    override suspend fun delete(documentId: String): Boolean {
        deletedDocumentIds += documentId
        return deleteSucceeds
    }

    override suspend fun cleanupStaging() = Unit
    override suspend fun cleanupOrphans(knownDocumentIds: Set<String>) {
        lastKnownDocumentIds = knownDocumentIds
    }
}
