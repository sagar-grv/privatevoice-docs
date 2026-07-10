package com.privatevoicedocs.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_packs")
data class ModelPackEntity(
    @PrimaryKey val id: String,
    val modelType: ModelType,
    val modelName: String,
    val version: String,
    val localPath: String,
    val sizeBytes: Long,
    val checksum: String,
    val installationStatus: InstallationStatus,
    val isActive: Boolean,
)
