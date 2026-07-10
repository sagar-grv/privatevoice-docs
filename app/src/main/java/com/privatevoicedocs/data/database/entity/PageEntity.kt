package com.privatevoicedocs.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pages",
    foreignKeys = [ForeignKey(
        entity = DocumentEntity::class,
        parentColumns = ["id"],
        childColumns = ["documentId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("documentId"), Index(value = ["documentId", "pageNumber"], unique = true)],
)
data class PageEntity(
    @PrimaryKey val id: String,
    val documentId: String,
    val pageNumber: Int,
    val rawText: String,
    val cleanedText: String,
    val extractionMethod: ExtractionMethod,
    val ocrConfidence: Float?,
    val processingStatus: ProcessingStatus,
)
