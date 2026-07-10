package com.privatevoicedocs.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.privatevoicedocs.data.database.entity.DocumentEntity
import com.privatevoicedocs.data.database.entity.ProcessingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun findById(id: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE fileHash = :hash LIMIT 1")
    suspend fun findByHash(hash: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE processingStatus = :status")
    suspend fun findByStatus(status: ProcessingStatus): List<DocumentEntity>

    @Query("SELECT id FROM documents")
    suspend fun allIds(): List<String>

    @Insert
    suspend fun insert(document: DocumentEntity)

    @Update
    suspend fun update(document: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteById(id: String)
}
