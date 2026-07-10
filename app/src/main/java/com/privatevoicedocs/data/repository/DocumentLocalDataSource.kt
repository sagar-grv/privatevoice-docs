package com.privatevoicedocs.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.privatevoicedocs.data.database.dao.DocumentDao
import com.privatevoicedocs.data.database.entity.DocumentEntity
import com.privatevoicedocs.data.database.entity.ProcessingStatus
import kotlinx.coroutines.flow.Flow

class DuplicateDocumentHashException(cause: Throwable) : Exception(cause)

interface DocumentLocalDataSource {
    fun observeAll(): Flow<List<DocumentEntity>>
    suspend fun findById(id: String): DocumentEntity?
    suspend fun findByHash(hash: String): DocumentEntity?
    suspend fun findDeleting(): List<DocumentEntity>
    suspend fun insert(document: DocumentEntity)
    suspend fun update(document: DocumentEntity)
    suspend fun deleteById(id: String)
}

class RoomDocumentLocalDataSource(private val dao: DocumentDao) : DocumentLocalDataSource {
    override fun observeAll(): Flow<List<DocumentEntity>> = dao.observeAll()
    override suspend fun findById(id: String): DocumentEntity? = dao.findById(id)
    override suspend fun findByHash(hash: String): DocumentEntity? = dao.findByHash(hash)
    override suspend fun findDeleting(): List<DocumentEntity> = dao.findByStatus(ProcessingStatus.DELETING)

    override suspend fun insert(document: DocumentEntity) {
        try {
            dao.insert(document)
        } catch (error: SQLiteConstraintException) {
            throw DuplicateDocumentHashException(error)
        }
    }

    override suspend fun update(document: DocumentEntity) = dao.update(document)
    override suspend fun deleteById(id: String) = dao.deleteById(id)
}
