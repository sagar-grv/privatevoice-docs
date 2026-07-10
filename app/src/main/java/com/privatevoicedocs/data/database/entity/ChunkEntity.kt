package com.privatevoicedocs.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chunks",
    foreignKeys = [ForeignKey(
        entity = DocumentEntity::class,
        parentColumns = ["id"],
        childColumns = ["documentId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("documentId"), Index(value = ["documentId", "pageNumber", "chunkIndex"], unique = true)],
)
data class ChunkEntity(
    @PrimaryKey val id: String,
    val documentId: String,
    val pageNumber: Int,
    val chunkIndex: Int,
    val sectionHeading: String?,
    val content: String,
    val tokenEstimate: Int,
    val startOffset: Int,
    val endOffset: Int,
    val embeddingBlob: ByteArray?,
    val createdAt: Long,
)
