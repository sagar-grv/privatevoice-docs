package com.privatevoicedocs.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "documents", indices = [Index(value = ["fileHash"], unique = true)])
data class DocumentEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val originalUri: String,
    val localFilePath: String,
    val fileHash: String,
    val mimeType: String,
    val fileSize: Long,
    val pageCount: Int?,
    val processingStatus: ProcessingStatus,
    val processingProgress: Int,
    val processingError: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
