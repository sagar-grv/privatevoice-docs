package com.privatevoicedocs.domain.repository

import com.privatevoicedocs.domain.model.DeleteDocumentResult
import com.privatevoicedocs.domain.model.Document
import com.privatevoicedocs.domain.model.ImportDocumentResult
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    val documents: Flow<List<Document>>
    suspend fun importDocument(sourceUri: String): ImportDocumentResult
    suspend fun deleteDocument(id: String): DeleteDocumentResult
    suspend fun reconcile()
}
