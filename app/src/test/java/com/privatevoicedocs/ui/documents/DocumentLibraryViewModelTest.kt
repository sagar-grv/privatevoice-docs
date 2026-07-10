package com.privatevoicedocs.ui.documents

import com.privatevoicedocs.MainDispatcherRule
import com.privatevoicedocs.data.database.entity.ProcessingStatus
import com.privatevoicedocs.domain.model.DeleteDocumentResult
import com.privatevoicedocs.domain.model.Document
import com.privatevoicedocs.domain.model.ImportDocumentResult
import com.privatevoicedocs.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class DocumentLibraryViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `documents flow into UI state and batch import reports per-item outcomes`() = runTest {
        val repository = FakeDocumentRepository().apply {
            documentsFlow.value = listOf(document("existing", "Existing.pdf"))
            importResults += ImportDocumentResult.Success(document("new", "New.pdf"))
            importResults += ImportDocumentResult.Duplicate("existing", "Existing.pdf")
            importResults += ImportDocumentResult.Failure("Unreadable file")
        }
        val viewModel = DocumentLibraryViewModel(repository)

        viewModel.importDocuments(listOf("content://new", "content://duplicate", "content://bad"))

        assertEquals(listOf("Existing.pdf"), viewModel.uiState.value.documents.map { it.displayName })
        assertEquals("Imported 1 · Duplicates 1 · Failed 1", viewModel.uiState.value.feedback)
        assertFalse(viewModel.uiState.value.isImporting)
    }
}

private class FakeDocumentRepository : DocumentRepository {
    val documentsFlow = MutableStateFlow<List<Document>>(emptyList())
    val importResults = ArrayDeque<ImportDocumentResult>()
    override val documents: Flow<List<Document>> = documentsFlow
    override suspend fun importDocument(sourceUri: String): ImportDocumentResult = importResults.removeFirst()
    override suspend fun deleteDocument(id: String): DeleteDocumentResult = DeleteDocumentResult.Success
    override suspend fun reconcile() = Unit
}

private fun document(id: String, name: String) = Document(
    id = id,
    displayName = name,
    mimeType = "application/pdf",
    fileSize = 100,
    pageCount = null,
    status = ProcessingStatus.IMPORTED,
    progress = 0,
    error = null,
    createdAt = 1,
)
