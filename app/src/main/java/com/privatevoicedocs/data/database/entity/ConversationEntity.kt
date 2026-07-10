package com.privatevoicedocs.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val selectedDocumentIds: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
)
