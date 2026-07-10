package com.privatevoicedocs.ui.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privatevoicedocs.domain.model.DeleteDocumentResult
import com.privatevoicedocs.domain.model.Document
import com.privatevoicedocs.domain.model.ImportDocumentResult
import com.privatevoicedocs.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DocumentLibraryUiState(
    val documents: List<Document> = emptyList(),
    val isLoading: Boolean = true,
    val isImporting: Boolean = false,
    val feedback: String? = null,
    val importOutcomes: List<ImportOutcome> = emptyList(),
)

data class ImportOutcome(
    val sourceUri: String,
    val label: String,
    val detail: String,
    val succeeded: Boolean,
)

private data class DocumentActionState(
    val isImporting: Boolean = false,
    val feedback: String? = null,
    val importOutcomes: List<ImportOutcome> = emptyList(),
)

class DocumentLibraryViewModel(
    private val repository: DocumentRepository,
) : ViewModel() {
    private val actionState = MutableStateFlow(DocumentActionState())

    val uiState: StateFlow<DocumentLibraryUiState> = combine(
        repository.documents,
        actionState,
    ) { documents, action ->
        DocumentLibraryUiState(
            documents = documents,
            isLoading = false,
            isImporting = action.isImporting,
            feedback = action.feedback,
            importOutcomes = action.importOutcomes,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DocumentLibraryUiState(),
    )

    fun importDocuments(sourceUris: List<String>) {
        if (sourceUris.isEmpty() || actionState.value.isImporting) return
        viewModelScope.launch {
            actionState.value = DocumentActionState(isImporting = true)
            var imported = 0
            var duplicates = 0
            var failed = 0
            val outcomes = mutableListOf<ImportOutcome>()
            sourceUris.forEach { sourceUri ->
                when (val result = repository.importDocument(sourceUri)) {
                    is ImportDocumentResult.Success -> {
                        imported++
                        outcomes += ImportOutcome(sourceUri, result.document.displayName, "Imported", succeeded = true)
                    }
                    is ImportDocumentResult.Duplicate -> {
                        duplicates++
                        outcomes += ImportOutcome(sourceUri, result.displayName, "Duplicate of an existing document", succeeded = false)
                    }
                    is ImportDocumentResult.Failure -> {
                        failed++
                        outcomes += ImportOutcome(sourceUri, sourceUri.substringAfterLast('/'), result.message, succeeded = false)
                    }
                }
            }
            actionState.value = DocumentActionState(
                feedback = "Imported $imported · Duplicates $duplicates · Failed $failed",
                importOutcomes = outcomes,
            )
        }
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            val feedback = when (repository.deleteDocument(id)) {
                DeleteDocumentResult.Success -> "Document deleted"
                DeleteDocumentResult.NotFound -> "Document was already deleted"
                is DeleteDocumentResult.Failure -> "Deletion failed. Tap delete to retry."
            }
            actionState.value = actionState.value.copy(feedback = feedback)
        }
    }

    fun clearFeedback() {
        actionState.value = actionState.value.copy(feedback = null)
    }
}
