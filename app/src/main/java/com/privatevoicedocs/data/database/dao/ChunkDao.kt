package com.privatevoicedocs.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.privatevoicedocs.data.database.entity.ChunkEntity

@Dao
interface ChunkDao {
    @Insert suspend fun insert(chunk: ChunkEntity)
    @Query("SELECT * FROM chunks WHERE documentId = :documentId ORDER BY pageNumber, chunkIndex")
    suspend fun forDocument(documentId: String): List<ChunkEntity>
    @Query("SELECT COUNT(*) FROM chunks WHERE documentId = :documentId")
    suspend fun countForDocument(documentId: String): Int
}
