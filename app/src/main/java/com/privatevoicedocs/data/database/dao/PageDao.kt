package com.privatevoicedocs.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.privatevoicedocs.data.database.entity.PageEntity

@Dao
interface PageDao {
    @Insert suspend fun insert(page: PageEntity)
    @Query("SELECT * FROM pages WHERE documentId = :documentId ORDER BY pageNumber")
    suspend fun forDocument(documentId: String): List<PageEntity>
    @Query("SELECT COUNT(*) FROM pages WHERE documentId = :documentId")
    suspend fun countForDocument(documentId: String): Int
}
